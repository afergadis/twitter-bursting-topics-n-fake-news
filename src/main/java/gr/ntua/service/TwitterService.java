package gr.ntua.service;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

public class TwitterService {

    private static final Logger LOGGER = Logger.getLogger(TwitterService.class);
    private Twitter twitter;

    public TwitterService() throws Exception {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("nYbEZcm9nB03x6axLGayTkMXf")
                .setOAuthConsumerSecret("3lUAUoyU7znn2GaAj8bZ1USJfBdC0BYoj3kc0g4QEvnFDjFUfD")
                .setOAuthAccessToken("3131540223-Y3hXSCE3wevNHTLbw4MAQ9Qa6iivxGkyganZXgw")
                .setOAuthAccessTokenSecret("ZtXw1VLmmqZzeCAhCJiC6YKrmq95ktDncdW1fRIQrxkWY");
        TwitterFactory twitterFactory = new TwitterFactory(cb.build());
        twitter = twitterFactory.getInstance();
    }

    public List<String> getTweets(String keyword) throws Exception {
        return getTweets(keyword, 5);
    }

    public List<String> getTweets(String keyword, int num) throws Exception {
        List<String> tweetsText = new ArrayList<>();
        try {
            Query query = new Query(keyword);
            query.setCount(num);
            query.setLang("en");
//            query.setSince();
//            query.setUntil();
            QueryResult result;
            result = twitter.search(query);
            List<Status> resultTweets = result.getTweets();
            for (Status tweet : resultTweets) {
                String text = tweet.getText().replaceAll("\n", "");
                tweetsText.add(text);
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
        return tweetsText;
    }
}