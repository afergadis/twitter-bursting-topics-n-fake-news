package gr.ntua.service;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TwitterService {
    private Twitter twitter;

    TwitterService() throws Exception {
        Properties credentials = new Properties();
        try (InputStream inputStream = TwitterService.class.getClassLoader().getResourceAsStream("credentials.properties")) {
            credentials.load(inputStream);
        }
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(credentials.getProperty("twitter.consumer_key"))
                .setOAuthConsumerSecret(credentials.getProperty("twitter.consumer_secret"))
                .setOAuthAccessToken(credentials.getProperty("twitter.access_token"))
                .setOAuthAccessTokenSecret(credentials.getProperty("twitter.access_token.secret"));
        TwitterFactory twitterFactory = new TwitterFactory(cb.build());
        twitter = twitterFactory.getInstance();
    }

    public List<String> getTweets(String keyword) throws Exception {
        return getTweets(keyword, 5);
    }

    List<String> getTweets(String keyword, int num) throws Exception {
        List<String> tweetsText = new ArrayList<>();
        try {
            Query query = new Query(keyword);
            query.setCount(num);
            query.setLang("en");
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