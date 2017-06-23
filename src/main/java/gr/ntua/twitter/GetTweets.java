package gr.ntua.twitter;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GetTweets {

    public static void GetTweets(String keyword) throws Exception {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("nYbEZcm9nB03x6axLGayTkMXf")
                .setOAuthConsumerSecret("3lUAUoyU7znn2GaAj8bZ1USJfBdC0BYoj3kc0g4QEvnFDjFUfD")
                .setOAuthAccessToken("3131540223-Y3hXSCE3wevNHTLbw4MAQ9Qa6iivxGkyganZXgw")
                .setOAuthAccessTokenSecret("ZtXw1VLmmqZzeCAhCJiC6YKrmq95ktDncdW1fRIQrxkWY");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        try {
            Query query = new Query(keyword);
            query.setCount(4);
            query.setLang("en");
//            query.setSince();
//            query.setUntil();
            QueryResult result;
            result = twitter.search(query);
            List<Status> tweets = result.getTweets();
            File unlabeledCsv = File.createTempFile("unlabeled", ".arff");
//            CSVWriter csvWriter = new CSVWriter(new FileWriter(unlabeledCsv));
            PrintWriter pw = new PrintWriter(unlabeledCsv);
            StringBuilder sb = new StringBuilder();
//            sb.append("title,");
            sb.append("@relation unlabeled\n\n");
            sb.append("@attribute anger numeric\n");
            sb.append("@attribute disgust numeric\n");
            sb.append("@attribute fear numeric\n");
            sb.append("@attribute joy numeric\n");
            sb.append("@attribute sadness numeric\n");
            sb.append("@attribute analytical numeric\n");
            sb.append("@attribute confident numeric\n");
            sb.append("@attribute tentative numeric\n");
            sb.append("@attribute openness numeric\n");
            sb.append("@attribute conscientiousness numeric\n");
            sb.append("@attribute extraversion numeric\n");
            sb.append("@attribute agreeableness numeric\n");
            sb.append("@attribute neuroticism numeric\n");
            sb.append("@attribute class {FAKE,REAL}\n\n");
            sb.append("@data");
            sb.append('\n');
            for (Status tweet : tweets) {
                //εδω είναι όλη η πληροφορία για κάθε tweet - από εδώ θα τραβήξει το App πληροφορίες
                String username = tweet.getUser().getScreenName();    //όνομα χρήστη
                int followers = tweet.getUser().getFollowersCount();    //followers count
                int friends = tweet.getUser().getFriendsCount(); //friends count
                int retweecnt = tweet.getRetweetCount();    //retweet count
                String text = tweet.getText();
                System.out.println("Analyzing tweet .." + text);

                //εδώ τρέχουμε το text του κάθε tweet στον analyzer και φτιάχνουμε το feature vector σε ένα csv αρχείο


                ToneAnalyzer service = new ToneAnalyzer("2016-02-11");
                service.setUsernameAndPassword("c04813a8-a1d6-40a3-b32e-43c2cbe7ed99", "Ah6a7DipbjBO");
                ToneAnalysis tone = service.getTone(text, null).execute();
//                System.out.println(tone);

                parseTones(sb, text, tone);
            }
            String[] split = sb.toString().split("\n");
            List<String[]> csv = new ArrayList<String[]>();
            for (int i = 0; i < split.length; i++) {
                String[] values = split[i].split(",");
                csv.add(values);
            }
//            csvWriter.writeAll(csv);
//            csvWriter.flush();
//            csvWriter.close();
            pw.write(sb.toString());
            pw.close();
            System.out.println("tweets to vectors done!");


            //εδώ τρέχουμε το μοντέλο με το feature vector
            String rootPath = GetTweets.class.getResource("/weka/RF.model").getPath();
            Classifier cls = (Classifier) weka.core.SerializationHelper.read(rootPath);

//            CSVLoader loader = new CSVLoader();
//            loader.setNoHeaderRowPresent(true);
            ArffLoader loader = new ArffLoader();
            loader.setSource(unlabeledCsv);
            Instances unlabeled = loader.getDataSet(); //new Instances(new BufferedReader(new FileReader(unlabeledCsv)));

            // set class attribute
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
//            StringToNominal stringToNominal = new StringToNominal();
//            stringToNominal.setInputFormat(unlabeled);
//            Instances filterUnlabeled = Filter.useFilter(unlabeled, stringToNominal);
            Instances filterUnlabeled = unlabeled;
            // create copy
            Instances labeled = new Instances(filterUnlabeled);

            // label instances
            for (int i = 0; i < filterUnlabeled.numInstances(); i++) {
                Instance instance = filterUnlabeled.instance(i);
                instance.setClassMissing();
                double clsLabel = cls.classifyInstance(instance);
                double[] doubles = cls.distributionForInstance(instance);
                labeled.instance(i).setClassValue(clsLabel);
            }
            // save labeled data
            File labeledCsv = File.createTempFile("labeled", ".csv");
            BufferedWriter writer = new BufferedWriter(new FileWriter(labeledCsv));
            writer.write(labeled.toString());
//            writer.newLine();
            writer.flush();
            writer.close();
            System.exit(0);

        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
    }

    private static void parseTones(StringBuilder sb, String text, ToneAnalysis tone) throws JSONException {
        JSONObject obj = new JSONObject(tone);
        JSONObject documentTone = (JSONObject) obj.get("documentTone");
        JSONArray tones = (JSONArray) documentTone.get("tones");
        JSONObject emotionTone = (JSONObject) tones.get(0);
        JSONObject writingTone = (JSONObject) tones.get(1);
        JSONObject socialTone = (JSONObject) tones.get(2);
        JSONArray emotionTones = (JSONArray) emotionTone.get("tones");
        JSONArray writingTones = (JSONArray) writingTone.get("tones");
        JSONArray socialTones = (JSONArray) socialTone.get("tones");
//        String title = text.replaceAll("[^\\x20-\\x7E]", "").replaceAll(",", "").replaceAll("\n", "");
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
        String label = "?";
//        sb.append(title).append(",");
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
        sb.append(label);
        sb.append('\n');
    }


    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub

        String keyword = "#LiNCLocal"; // εδώ θα μπαίνει το bursting topic
        GetTweets(keyword);

    }

}