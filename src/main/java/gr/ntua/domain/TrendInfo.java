package gr.ntua.domain;

/**
 * Created by katerina on 6/22/17.
 */
public class TrendInfo {
    private Trend trend;
    private Iterable<Tweet> tweets;

    public TrendInfo() {
        this.trend = new Trend();
        this.trend.setName("TOPIC_1");
    }
    public Trend getTrend() {
        return trend;
    }

    public void setTrend(Trend trend) {
        this.trend = trend;
    }

    public Iterable<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(Iterable<Tweet> tweets) {
        this.tweets = tweets;
    }
}
