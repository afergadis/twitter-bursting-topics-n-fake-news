package gr.ntua.domain;

/**
 * Created by katerina on 6/22/17.
 */
public class Tweet {
    private String message;
    private double fakeScore;

    public Tweet() {
        message = "";
        fakeScore = 0.0;
    }

    public Tweet(String message, double fakeScore) {
        this.message = message;
        this.fakeScore = fakeScore;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getFakeScore() {
        return fakeScore;
    }

    public void setFakeScore(double fakeScore) {
        this.fakeScore = Math.round(fakeScore*100.0);
    }
}
