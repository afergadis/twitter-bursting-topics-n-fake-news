package gr.ntua.watson;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by aris on 24/6/2017.
 */
public class ToneVectorizer {
    private static final Logger LOGGER = Logger.getLogger(ToneVectorizer.class.getName());
    private final StringBuilder arffHeader;
    private ToneAnalyzer service;

    public ToneVectorizer() {
        service = new ToneAnalyzer("2016-02-11");
        service.setUsernameAndPassword("c04813a8-a1d6-40a3-b32e-43c2cbe7ed99", "Ah6a7DipbjBO");

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

    public File vectorize(List<String> tweetsText) throws JSONException, IOException {
        StringBuilder tweetVector = new StringBuilder();
        tweetVector.append(arffHeader);
        for (String text : tweetsText) {
            tweetVector.append(getTones(text));
            LOGGER.info(text);
        }
        File unlabeledArff = File.createTempFile("unlabeled", ".arff");
        PrintWriter pw = new PrintWriter(unlabeledArff);
        pw.write(tweetVector.toString());
        pw.close();
        LOGGER.info("Tweets to vectors done!");
        return unlabeledArff;
    }

    private StringBuilder getTones(String text) throws JSONException {
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
