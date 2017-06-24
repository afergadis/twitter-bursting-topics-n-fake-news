package gr.ntua.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by katerina on 6/22/17.
 */
public class TrendInfo {
    private Trend trend;
    private String image;
    private List<Tweet> tweets = new ArrayList<Tweet>();

    public TrendInfo(Trend trend) {
        this.trend = trend;
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

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public void addTweet(Tweet tweet) {
        this.tweets.add(tweet);
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
