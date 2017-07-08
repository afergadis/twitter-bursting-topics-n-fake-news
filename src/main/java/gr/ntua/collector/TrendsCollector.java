package gr.ntua.collector;

import gr.ntua.domain.Trend;
import gr.ntua.service.TrendService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by aris on 18/6/2017.
 */
@Component
public class TrendsCollector {
    private final Logger LOGGER = Logger.getLogger(TrendsCollector.class.getName());
    private final TrendService trendService;
    private String TWITTER_CONSUMER_KEY;
    private String TWITTER_CONSUMER_SECRET;
    private String TRENDS_PLACE_ID;

    @Autowired
    public TrendsCollector(TrendService trendService) {
        this.trendService = trendService;
        Properties credentials = new Properties();
        try (InputStream input = TrendsCollector.class.getClassLoader().getResourceAsStream("credentials.properties")) {
            credentials.load(input);
            TWITTER_CONSUMER_KEY = credentials.getProperty("twitter.consumer_key");
            TWITTER_CONSUMER_SECRET = credentials.getProperty("twitter.consumer_secret");
            TRENDS_PLACE_ID = "https://api.twitter.com/1.1/trends/place.json?id=" + credentials.getProperty("twitter.trends.place.id");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 7200000)
    public void collectTrends() {
        String jsonResponse = getTimelineForSearchTerm();
        jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);
        try {
            JSONObject obj = new JSONObject(jsonResponse);
            JSONArray arr = obj.getJSONArray("trends");
            for (int i = 0; i < arr.length(); i++) {
                try {
                    Integer tweet_volume = arr.getJSONObject(i).getInt("tweet_volume");
                    String name = arr.getJSONObject(i).getString("name");
                    Trend trend = new Trend(name, tweet_volume);
                    Trend save = trendService.save(trend);
                    trendService.updateBursting(trend);
                    LOGGER.info(save.toString());
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            System.out.println("YOUHOUH");
        }
    }

    private String appAuthentication() {
        HttpURLConnection httpConnection = null;
        OutputStream outputStream;
        BufferedReader bufferedReader;
        StringBuilder response = null;

        try {
            String URL_ROOT_TWITTER_API = "https://api.twitter.com";
            String URL_AUTHENTICATION = URL_ROOT_TWITTER_API + "/oauth2/token";
            URL url = new URL(URL_AUTHENTICATION);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            String CONSUMER_KEY = TWITTER_CONSUMER_KEY;
            String CONSUMER_SECRET = TWITTER_CONSUMER_SECRET;
            String accessCredential = CONSUMER_KEY + ":" + CONSUMER_SECRET;
            String authorization = "Basic " + Base64.getEncoder().encodeToString(accessCredential.getBytes());
            String param = "grant_type=client_credentials";
            httpConnection.addRequestProperty("Authorization", authorization);
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            httpConnection.connect();

            outputStream = httpConnection.getOutputStream();
            outputStream.write(param.getBytes());
            outputStream.flush();
            outputStream.close();
            // int statusCode = httpConnection.getResponseCode();
            // String reason =httpConnection.getResponseMessage();

            bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String line;
            response = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
        } catch (Exception ignored) {
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return response != null ? response.toString() : null;
    }

    private String getTimelineForSearchTerm() {
        HttpURLConnection httpConnection = null;
        BufferedReader bufferedReader;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(TRENDS_PLACE_ID);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");

            String jsonString = appAuthentication();
            JSONObject jsonObjectDocument = new JSONObject(jsonString);
            String token = jsonObjectDocument.getString("token_type") + " " + jsonObjectDocument.getString("access_token");
            httpConnection.setRequestProperty("Authorization", token);

            httpConnection.setRequestProperty("Authorization", token);
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
        } catch (Exception ignored) {
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return response.toString();
    }
}
