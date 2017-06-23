package gr.ntua.collector;

import gr.ntua.domain.Tweet;

import java.util.List;

/**
 * Created by aris on 20/6/2017.
 */
public class TestGetTweets {
    public static void main(String args[]) throws Exception {
        GetTweets getTweets = new GetTweets();
        List<Tweet> tweets = getTweets.getTweets("#LiNCLocal");
        for (Tweet tweet : tweets) {
            System.out.println(tweet.getFakeScore() + ": " + tweet.getMessage());
        }
    }
}
