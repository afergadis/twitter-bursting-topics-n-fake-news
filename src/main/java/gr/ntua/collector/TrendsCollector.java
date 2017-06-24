package gr.ntua.collector;

import gr.ntua.domain.Trend;
import gr.ntua.repository.TrendRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * Created by aris on 18/6/2017.
 */
@Component
public class TrendsCollector {
    private final Logger LOGGER = Logger.getLogger(TrendsCollector.class.getName());
    private final String URL_ROOT_TWITTER_API = "https://api.twitter.com";
    //    private final String URL_SEARCH = URL_ROOT_TWITTER_API + "/1.1/search/tweets.json?q=";
    private final String URL_AUTHENTICATION = URL_ROOT_TWITTER_API + "/oauth2/token";
    private final String URL_INDIA_TRENDING = "https://api.twitter.com/1.1/trends/place.json?id=23424975";
    private final String CONSUMER_KEY = "nYbEZcm9nB03x6axLGayTkMXf";
    private final String CONSUMER_SECRET = "3lUAUoyU7znn2GaAj8bZ1USJfBdC0BYoj3kc0g4QEvnFDjFUfD";
    //    private final String TAG = "TwitterUtils";
    private final TrendRepository trendRepository;
    private Long counter = Long.valueOf(0);

    @Autowired
    public TrendsCollector(TrendRepository trendRepository) {
        this.trendRepository = trendRepository;
    }

    @Scheduled(fixedRate = 7200000)
    public void collectTrends() {
        counter++;
        String jsonresponse = getTimelineForSearchTerm();
        jsonresponse = jsonresponse.substring(1, jsonresponse.length() - 1);
        try {
            JSONObject obj = new JSONObject(jsonresponse);
            JSONArray arr = obj.getJSONArray("trends");
            for (int i = 0; i < arr.length(); i++) {
                try {
                    Integer tweet_volume = arr.getJSONObject(i).getInt("tweet_volume");
                    String name = arr.getJSONObject(i).getString("name");
                    Trend trend = new Trend(counter, name, tweet_volume);
                    Trend save = trendRepository.save(trend);
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
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder response = null;

        try {
            URL url = new URL(URL_AUTHENTICATION);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

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
            URL url = new URL(URL_INDIA_TRENDING);
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
