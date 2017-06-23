package gr.ntua.service;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import gr.ntua.domain.Tweet;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TwitterService {

    private static final Logger LOGGER = Logger.getLogger(TwitterService.class);
    private final StringBuilder arffHeader;
    private Twitter twitter;
    private ToneAnalyzer service;
    private Classifier cls;

    public TwitterService() throws Exception {
        String wekaModel = TwitterService.class.getResource("/weka/RF.model").getPath();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("nYbEZcm9nB03x6axLGayTkMXf")
                .setOAuthConsumerSecret("3lUAUoyU7znn2GaAj8bZ1USJfBdC0BYoj3kc0g4QEvnFDjFUfD")
                .setOAuthAccessToken("3131540223-Y3hXSCE3wevNHTLbw4MAQ9Qa6iivxGkyganZXgw")
                .setOAuthAccessTokenSecret("ZtXw1VLmmqZzeCAhCJiC6YKrmq95ktDncdW1fRIQrxkWY");
        TwitterFactory twitterFactory = new TwitterFactory(cb.build());
        twitter = twitterFactory.getInstance();
        service = new ToneAnalyzer("2016-02-11");
        service.setUsernameAndPassword("c04813a8-a1d6-40a3-b32e-43c2cbe7ed99", "Ah6a7DipbjBO");
        cls = (Classifier) weka.core.SerializationHelper.read(wekaModel);

        arffHeader = new StringBuilder();
        arffHeader.append("@relation unlabeled\n\n");
        arffHeader.append("@attribute anger numeric\n");
        arffHeader.append("@attribute disgust numeric\n");
        arffHeader.append("@attribute fear numeric\n");
        arffHeader.append("@attribute joy numeric\n");
        arffHeader.append("@attribute sadness numeric\n");
        arffHeader.append("@attribute analytical numeric\n");
        arffHeader.append("@attribute confident numeric\n");
        arffHeader.append("@attribute tentative numeric\n");
        arffHeader.append("@attribute openness numeric\n");
        arffHeader.append("@attribute conscientiousness numeric\n");
        arffHeader.append("@attribute extraversion numeric\n");
        arffHeader.append("@attribute agreeableness numeric\n");
        arffHeader.append("@attribute neuroticism numeric\n");
        arffHeader.append("@attribute class {FAKE,REAL}\n\n");
        arffHeader.append("@data");
        arffHeader.append('\n');
    }

    public List<Tweet> getTweets(String keyword) throws Exception {
        return getTweets(keyword, 5);
    }

    public List<Tweet> getTweets(String keyword, int num) throws Exception {
        try {
            Query query = new Query(keyword);
            query.setCount(num);
            query.setLang("en");
//            query.setSince();
//            query.setUntil();
            QueryResult result;
            result = twitter.search(query);
            List<Status> resultTweets = result.getTweets();
            List<Tweet> scoredTweets = new ArrayList<>();
            StringBuilder tweetVector = new StringBuilder();
            tweetVector.append(arffHeader);
            for (Status tweet : resultTweets) {
                String text = tweet.getText().replaceAll("\n", "");
                Tweet scoredTweet = new Tweet();
                scoredTweet.setMessage(text);
                scoredTweets.add(scoredTweet);
                LOGGER.info("Analyzing tweet: " + text);
                tweetVector.append(getTone(text));
            }
            File unlabeledArff = File.createTempFile("unlabeled", ".arff");
            PrintWriter pw = new PrintWriter(unlabeledArff);
            pw.write(tweetVector.toString());
            pw.close();
            System.out.println("Tweets to vectors done! Classifying...");

            ArffLoader arffLoader = new ArffLoader();
            arffLoader.setSource(unlabeledArff);
            Instances unlabeled = arffLoader.getDataSet();

            // set class attribute
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
            // label instances
            for (int i = 0; i < unlabeled.numInstances(); i++) {
                double[] predictions = cls.distributionForInstance(unlabeled.instance(i));
                scoredTweets.get(i).setFakeScore(predictions[0]);
            }
            return scoredTweets;
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            return new ArrayList<>();
        }
    }

    private StringBuilder getTone(String text) throws JSONException {
        ToneAnalysis tone = service.getTone(text, null).execute();

        JSONObject obj = new JSONObject(tone);
        JSONObject documentTone = (JSONObject) obj.get("documentTone");
        JSONArray tones = (JSONArray) documentTone.get("tones");
        JSONObject emotionTone = (JSONObject) tones.get(0);
        JSONObject writingTone = (JSONObject) tones.get(1);
        JSONObject socialTone = (JSONObject) tones.get(2);
        JSONArray emotionTones = (JSONArray) emotionTone.get("tones");
        JSONArray writingTones = (JSONArray) writingTone.get("tones");
        JSONArray socialTones = (JSONArray) socialTone.get("tones");

        double anger = 0.00;
        double disgust = 0.00;
        double fear = 0.00;
        double joy = 0.00;
        double sadness = 0.00;
        double analytical = 0.00;
        double confident = 0.00;
        double tentative = 0.00;
        double openness = 0.00;
        double conscientiousness = 0.00;
        double extraversion = 0.00;
        double agreeableness = 0.00;
        double neuroticism = 0.00;

        for (int i = 0; i < emotionTones.length(); i++) {
            JSONObject jsonObject = emotionTones.getJSONObject(i);
            Double score = (Double) jsonObject.get("score");
            String emotion = (String) jsonObject.get("id");
            switch (emotion) {
                case "anger":
                    anger = score;
                    break;
                case "disgust":
                    disgust = score;
                    break;
                case "fear":
                    fear = score;
                    break;
                case "joy":
                    joy = score;
                    break;
                case "sadness":
                    sadness = score;
                    break;
            }
        }
        for (int i = 0; i < writingTones.length(); i++) {
            JSONObject jsonObject = writingTones.getJSONObject(i);
            Double score = (Double) jsonObject.get("score");
            String writing = (String) jsonObject.get("id");
            switch (writing) {
                case "analytical":
                    analytical = score;
                    break;
                case "confident":
                    confident = score;
                    break;
                case "tentative":
                    tentative = score;
                    break;
            }
        }
        for (int i = 0; i < socialTones.length(); i++) {
            JSONObject jsonObject = socialTones.getJSONObject(i);
            Double score = (Double) jsonObject.get("score");
            String social = (String) jsonObject.get("id");
            switch (social) {
                case "openness_big5":
                    openness = score;
                    break;
                case "conscientiousness_big5":
                    conscientiousness = score;
                    break;
                case "extraversion_big5":
                    extraversion = score;
                    break;
                case "agreeableness_big5":
                    agreeableness = score;
                    break;
                case "neuroticism_big5":
                    neuroticism = score;
                    break;
            }
        }
        StringBuilder toneScores = new StringBuilder();
        toneScores.append(anger).append(",");
        toneScores.append(disgust).append(",");
        toneScores.append(fear).append(",");
        toneScores.append(joy).append(",");
        toneScores.append(sadness).append(",");
        toneScores.append(analytical).append(",");
        toneScores.append(confident).append(",");
        toneScores.append(tentative).append(",");
        toneScores.append(openness).append(",");
        toneScores.append(conscientiousness).append(",");
        toneScores.append(extraversion).append(",");
        toneScores.append(agreeableness).append(",");
        toneScores.append(neuroticism).append(",");
        toneScores.append("?\n");

        return toneScores;
    }
}