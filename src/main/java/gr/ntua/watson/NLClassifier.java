package gr.ntua.watson;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;
import gr.ntua.domain.Tweet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by aris on 23/6/2017.
 */
public class NLClassifier {
    private static final Logger LOGGER = Logger.getLogger(NLClassifier.class.getName());
    private String ClassifierID = "67a480x203-nlc-68882";
    private NaturalLanguageClassifier service;

    public NLClassifier() {
        Properties credentials = new Properties();
        try (InputStream inputStream = NLClassifier.class.getClassLoader().getResourceAsStream("credentials.properties")) {
            credentials.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClassifierID = credentials.getProperty("bluemix.natural_language_classifier.classifier_id");
        service = new NaturalLanguageClassifier();
        service.setUsernameAndPassword(credentials.getProperty("bluemix.natural_language_classifier.username"),
                credentials.getProperty("bluemix.natural_language_classifier.password"));
    }

    public static void main(String[] args) {
        List<String> tweetsText = new ArrayList<>();
        tweetsText.add("Slater is a huge money laundering problem for POS Trump");
        tweetsText.add("How This Millennial CEO Taps Into The Power Of Work-Life Balance @Forbes @RachelRitlop ");
        tweetsText.add("How To Create A Virtual Internship That Will Change Your Life ");

        NLClassifier classifier = new NLClassifier();
        List<Tweet> tweets = classifier.classify(tweetsText);
        for (Tweet tweet : tweets) {
            System.out.println(tweet.getFakeScore() + ": " + tweet.getMessage());
        }
    }

    public List<Tweet> classify(List<String> tweetsText) {
        List<Tweet> tweets = new ArrayList<>();
        for (String text : tweetsText) {
            Classification classification = service.classify(ClassifierID, text).execute();
            Tweet tweet = new Tweet();
            tweet.setMessage(text);
            String className = classification.getClasses().get(0).getName();
            if (className.equals("FAKE"))
                tweet.setFakeScore(classification.getClasses().get(0).getConfidence());
            else
                tweet.setFakeScore(classification.getClasses().get(1).getConfidence());
            tweets.add(tweet);
            LOGGER.info(tweet.getFakeScore() + ": " + tweet.getMessage());
        }
        return tweets;
    }
}
