package gr.ntua.collector;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GetTweets {

    private String wekaModel;
    private ConfigurationBuilder cb;
    private TwitterFactory twitterFactory;
    private Twitter twitter;
    private ToneAnalyzer service;
    private Classifier cls;

    public GetTweets() throws Exception {
        wekaModel = GetTweets.class.getResource("/weka/RandomForest_0.701_ROC.model").getPath();
        cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("nYbEZcm9nB03x6axLGayTkMXf")
                .setOAuthConsumerSecret("3lUAUoyU7znn2GaAj8bZ1USJfBdC0BYoj3kc0g4QEvnFDjFUfD")
                .setOAuthAccessToken("3131540223-Y3hXSCE3wevNHTLbw4MAQ9Qa6iivxGkyganZXgw")
                .setOAuthAccessTokenSecret("ZtXw1VLmmqZzeCAhCJiC6YKrmq95ktDncdW1fRIQrxkWY");
        twitterFactory = new TwitterFactory(cb.build());
        twitter = twitterFactory.getInstance();
        service = new ToneAnalyzer("2016-02-11");
        service.setUsernameAndPassword("c04813a8-a1d6-40a3-b32e-43c2cbe7ed99", "Ah6a7DipbjBO");
        cls = (Classifier) weka.core.SerializationHelper.read(wekaModel);
    }

    public void getTweets(String keyword) throws Exception {
        getTweets(keyword, 5);
    }

    public List<String> getTweets(String keyword, int num) throws Exception {
        try {
            Query query = new Query(keyword);
            query.setCount(num);
            query.setLang("en");
            QueryResult result;
            result = twitter.search(query);
            List<Status> tweets = result.getTweets();
            StringBuilder sb = new StringBuilder();
            sb.append("title,anger,disgust,fear,joy,sadness,");
            sb.append("analytical,confident,tentative,openness,");
            sb.append("conscientiousness,extraversion,agreeableness,");
            sb.append("neuroticism,label\n");
            for (Status tweet : tweets) {
                //εδω είναι όλη η πληροφορία για κάθε tweet - από εδώ θα τραβήξει το App πληροφορίες
                String username = tweet.getUser().getScreenName();    //όνομα χρήστη
                int followers = tweet.getUser().getFollowersCount();    //followers count
                int friends = tweet.getUser().getFriendsCount(); //friends count
                int retweecnt = tweet.getRetweetCount();    //retweet count
                String text = tweet.getText();
                System.out.println("Analyzing tweet: " + text);

                //εδώ τρέχουμε το text του κάθε tweet στον analyzer και φτιάχνουμε το feature vector σε ένα csv αρχείο
                ToneAnalysis tone = service.getTone(text, null).execute();
                sb.append(parseTones(text, tone));
            }
            System.out.println("Tweets to vectors done! Classifying...");
            File labeled = classify(sb);
            Scanner scanner = new Scanner(labeled);
            List<String> results = new ArrayList<>();
            while (scanner.hasNext()) {
                results.add(scanner.next());
            }
            scanner.close();
            return results;
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            return new ArrayList<>();
        }
    }

    private StringBuilder parseTones(String text, ToneAnalysis tone) throws JSONException {
        StringBuilder sb = new StringBuilder();

        JSONObject obj = new JSONObject(tone);
        JSONObject documentTone = (JSONObject) obj.get("documentTone");
        JSONArray tones = (JSONArray) documentTone.get("tones");
        JSONObject emotionTone = (JSONObject) tones.get(0);
        JSONObject writingTone = (JSONObject) tones.get(1);
        JSONObject socialTone = (JSONObject) tones.get(2);
        JSONArray emotionTones = (JSONArray) emotionTone.get("tones");
        JSONArray writingTones = (JSONArray) writingTone.get("tones");
        JSONArray socialTones = (JSONArray) socialTone.get("tones");

        String title = "?"; //text.replaceAll("[^\\x20-\\x7E]", "");
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
            if (emotion.equals("anger")) {
                anger = score;
            } else if (emotion.equals("disgust")) {
                disgust = score;
            } else if (emotion.equals("fear")) {
                fear = score;
            } else if (emotion.equals("joy")) {
                joy = score;
            } else if (emotion.equals("sadness")) {
                sadness = score;
            }
        }
        for (int i = 0; i < writingTones.length(); i++) {
            JSONObject jsonObject = writingTones.getJSONObject(i);
            Double score = (Double) jsonObject.get("score");
            String writing = (String) jsonObject.get("id");
            if (writing.equals("analytical")) {
                analytical = score;
            } else if (writing.equals("confident")) {
                confident = score;
            } else if (writing.equals("tentative")) {
                tentative = score;
            }
        }
        for (int i = 0; i < socialTones.length(); i++) {
            JSONObject jsonObject = socialTones.getJSONObject(i);
            Double score = (Double) jsonObject.get("score");
            String social = (String) jsonObject.get("id");
            if (social.equals("openness_big5")) {
                openness = score;
            } else if (social.equals("conscientiousness_big5")) {
                conscientiousness = score;
            } else if (social.equals("extraversion_big5")) {
                extraversion = score;
            } else if (social.equals("agreeableness_big5")) {
                agreeableness = score;
            } else if (social.equals("neuroticism_big5")) {
                neuroticism = score;
            }
        }
        String label = "";
        sb.append("\"").append(title).append("\",");
        sb.append(anger).append(",");
        sb.append(disgust).append(",");
        sb.append(fear).append(",");
        sb.append(joy).append(",");
        sb.append(sadness).append(",");
        sb.append(analytical).append(",");
        sb.append(confident).append(",");
        sb.append(tentative).append(",");
        sb.append(openness).append(",");
        sb.append(conscientiousness).append(",");
        sb.append(extraversion).append(",");
        sb.append(agreeableness).append(",");
        sb.append(neuroticism).append(",");
        sb.append(label).append('\n');

        return sb;
    }

    private File classify(StringBuilder sb) throws Exception {
        File unlabeledCsv = File.createTempFile("unlabeled", ".csv");
        File labeledCsv = File.createTempFile("labeled", ".csv");
        PrintWriter pw = new PrintWriter(unlabeledCsv);
        pw.write(sb.toString());
        pw.close();

        Instances unlabeled = new Instances(new BufferedReader(new FileReader(unlabeledCsv))); //προσοχή να αλλάξεις τα paths

        // set class attribute
        unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

        // create copy
        Instances labeled = new Instances(unlabeled);

        // label instances
        for (int i = 0; i < unlabeled.numInstances(); i++) {
            double clsLabel = cls.classifyInstance(unlabeled.instance(i));
            labeled.instance(i).setClassValue(clsLabel);
        }
        // save labeled data
        BufferedWriter writer = new BufferedWriter(new FileWriter(labeledCsv)); //προσοχή να αλλάξεις τα paths
        writer.write(labeled.toString());
        writer.newLine();
        writer.flush();
        writer.close();

        return labeledCsv;
    }
}