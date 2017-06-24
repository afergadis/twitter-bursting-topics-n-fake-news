package gr.ntua.nlc;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;

import java.util.logging.Logger;

/**
 * Created by aris on 23/6/2017.
 */
public class NLClassifier {
    private static final Logger LOGGER = Logger.getLogger(NLClassifier.class.getName());
    private static final String ClassifierID = "67a480x203-nlc-68882";
    NaturalLanguageClassifier service;

    public NLClassifier() {
        service = new NaturalLanguageClassifier();
        service.setUsernameAndPassword("9a6f8181-4140-4c43-aaaa-7ba8211f73b8", "7N8NbZMiovz1");
    }

    public static void main(String[] args) {
        NLClassifier nlClassifier = new NLClassifier();
        nlClassifier.classify("WaPo: Obama admin 'choked' on Russia, former official says");
    }

    public void classify(String text) {
        Classification classification = service.classify(ClassifierID, text).execute();
        LOGGER.info(classification.getTopClass() + ": " + classification.getText());
    }
}
