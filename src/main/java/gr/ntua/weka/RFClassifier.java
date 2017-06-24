package gr.ntua.weka;

import gr.ntua.domain.Tweet;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by aris on 24/6/2017.
 */
public class RFClassifier {
    private static final Logger LOGGER = Logger.getLogger(RFClassifier.class.getName());
    private Classifier classifier;

    public RFClassifier() {
        String wekaModel = RFClassifier.class.getResource("/weka/RF.model").getPath();
        try {
            classifier = (Classifier) weka.core.SerializationHelper.read(wekaModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Tweet> classify(List<String> tweetsText, File unlabeledArff) throws Exception {
        ArffLoader arffLoader = new ArffLoader();
        List<Tweet> tweets = new ArrayList<>();
        try {
            arffLoader.setSource(unlabeledArff);
            Instances unlabeled = arffLoader.getDataSet();
            // set class attribute
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
            // label instances
            for (int i = 0; i < unlabeled.numInstances(); i++) {
                Tweet tweet = new Tweet();
                tweet.setMessage(tweetsText.get(i));
                double[] predictions = classifier.distributionForInstance(unlabeled.instance(i));
                tweet.setFakeScore(predictions[0]);
                tweets.add(tweet);
                LOGGER.info(tweet.getFakeScore() + ": " + tweet.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tweets;
    }
}
