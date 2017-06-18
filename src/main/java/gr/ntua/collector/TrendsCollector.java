package gr.ntua.collector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aris on 18/6/2017.
 */
public class TrendsCollector {
    public static final String TAG = "TwitterUtils";
    private static int counter = 0;

    private static String appAuthentication() {

        HttpURLConnection httpConnection = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder response = null;

        try {
            URL url = new URL(ConstantsUtils.URL_AUTHENTICATION);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            String accessCredential = ConstantsUtils.CONSUMER_KEY + ":"
                    + ConstantsUtils.CONSUMER_SECRET;
            String authorization = "Basic "
                    + Base64.getEncoder().encodeToString(accessCredential.getBytes());
            String param = "grant_type=client_credentials";
            httpConnection.addRequestProperty("Authorization", authorization);
            httpConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
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

    private static String getTimelineForSearchTerm() {
        HttpURLConnection httpConnection = null;
        BufferedReader bufferedReader = null;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(ConstantsUtils.URL_INDIA_TRENDING);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");

            String jsonString = appAuthentication();
            JSONObject jsonObjectDocument = new JSONObject(jsonString);
            String token = jsonObjectDocument.getString("token_type") + " " + jsonObjectDocument.getString("access_token");
            httpConnection.setRequestProperty("Authorization", token);

            httpConnection.setRequestProperty("Authorization", token);
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(
                    httpConnection.getInputStream()));

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

    private static Connection connect() throws ClassNotFoundException {
        Connection conn = null;
        try {
            // db parameters
            // String url = "jdbc:sqlite:C:/Program Files/DB Browser for SQLite/bursting.db";
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://r-1tee-asprop.att.sch.gr:43306/twitter_trends", "bigdata", "bigdata17");


            System.out.println("Connection to mySQL has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void main(String[] args) throws JSONException {
        Timer timer = new Timer();
        TrendsCollector coll = new TrendsCollector();
        timer.schedule(coll.new trend_collector(), 0, 7200000);
    }

    private void insert(Connection conn, long curr_timespan, String curr_trend_name, long curr_trend_volume, double curr_is_bursting) {
        String sql = "INSERT INTO trends(timespan_id,trend_name,trend_volume, is_bursting) VALUES(?,?,?,?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, curr_timespan);
            pstmt.setString(2, curr_trend_name);
            pstmt.setLong(3, curr_trend_volume);
            pstmt.setDouble(4, curr_is_bursting);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public class ConstantsUtils {
        static final String URL_ROOT_TWITTER_API = "https://api.twitter.com";
        public static final String URL_SEARCH = URL_ROOT_TWITTER_API + "/1.1/search/tweets.json?q=";
        static final String URL_AUTHENTICATION = URL_ROOT_TWITTER_API + "/oauth2/token";
        static final String URL_INDIA_TRENDING = "https://api.twitter.com/1.1/trends/place.json?id=23424975";
        static final String CONSUMER_KEY = "nYbEZcm9nB03x6axLGayTkMXf";
        static final String CONSUMER_SECRET = "3lUAUoyU7znn2GaAj8bZ1USJfBdC0BYoj3kc0g4QEvnFDjFUfD";
    }

    class trend_collector extends TimerTask {

        public void run() {
            System.out.println("Collecting...");
            counter++;
            System.out.println("Timespan:" + counter);
            String jsonresponse = getTimelineForSearchTerm();
            jsonresponse = jsonresponse.substring(1, jsonresponse.length() - 1);
            // 		System.out.println(jsonresponse);
            try {
                JSONObject obj = new JSONObject(jsonresponse);
                JSONArray arr = obj.getJSONArray("trends");
                Connection conn = connect();
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        int tweet_volume = arr.getJSONObject(i).getInt("tweet_volume");
                        String name = arr.getJSONObject(i).getString("name");
                        System.out.println(name);
                        System.out.println(tweet_volume);
                        Double boo = 0.0;
                        insert(conn, counter, name, tweet_volume, boo);
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception e) {
                System.out.println("YOUHOUH");
            }
        }
    }
}
