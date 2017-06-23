package gr.ntua.domain;

/**
 * Created by katerina on 6/22/17.
 */
public class Tweet {
    private String message;
    private double fakeScore;

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
        this.fakeScore = fakeScore;
    }
}
