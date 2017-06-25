package gr.ntua.service;

import gr.ntua.domain.Trend;
import gr.ntua.domain.TrendInfo;
import gr.ntua.domain.Tweet;
import gr.ntua.repository.TrendRepository;
import gr.ntua.watson.NLClassifier;
import gr.ntua.watson.ToneVectorizer;
import gr.ntua.weka.RFClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by aris on 13/6/2017.
 */
@Service
public class TrendService {
    private final Logger LOGGER = Logger.getLogger(TrendService.class.getName());
    private final TrendRepository trendRepository;
    private TwitterService tweetsService;
    private ChartService chartService;

    @Autowired
    public TrendService(TrendRepository trendRepository) throws Exception {
        this.trendRepository = trendRepository;
        tweetsService = new TwitterService();
        this.chartService = new ChartService();
    }

    /* Finds records that have 0.0 in `is_bursting` column. For every record find the
     * most recent appearance searching by name and sorting by ids descending. If no
     * record is found, then this is a first seen trend.
     */
    public void updateBursting(Trend trend) {
        // Find by name the previous instances of that trend
        List<Trend> trendInstances = trendRepository.findByNameAndIdLessThanEqualOrderByIdAsc(
                trend.getName(), trend.getId());
        // First seen trend if there are no previous instances
        if (trendInstances.size() == 1) {
            trend.setBursting(100.0);
            trend.setFirstSeen();
            trendRepository.save(trend);
        }
        // Get two cascading trends and calculate percent change between previous and current instance.
        for (int i = 0; i < trendInstances.size() - 1; i++) {
            Trend previous = trendInstances.get(i);
            Trend current = trendInstances.get(i + 1);
            if (current.getBursting() != 0.0)
                continue;
            double percentChange = (current.getVolume() / previous.getVolume().doubleValue() - 1) * 100;
            current.setBursting(percentChange);
            trendRepository.save(current);
        }
    }

    public List<Date> getDateFromTo() {
        List<Date> dates = new ArrayList<>();
        Trend firstTrend = trendRepository.findTopByOrderByIdAsc();
        Trend lastTrend = trendRepository.findTopByOrderByIdDesc();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date from = sdf.parse(sdf.format(firstTrend.getDateTime()));
            Date to = sdf.parse(sdf.format(lastTrend.getDateTime()));
            // If its the same date, then change `to` to tomorrow
            if (from.equals(to)) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(to);
                calendar.add(Calendar.DATE, 1);
                to = calendar.getTime();
            }
            dates.add(from);
            dates.add(to);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dates;
    }

    public Iterable<Trend> getBursting(Double percent, Date from, Date to) {
        List<Date> dates = getDateFromTo();
        if (from == null)
            from = dates.get(0);
        if (to == null)
            to = dates.get(1);
        List<Trend> burstingTrends = trendRepository.findByBurstingGreaterThanEqualAndDateTimeBetween(percent, from, to);
        return burstingTrends;
    }

    public TrendInfo getTrendInfo(Long trend_id) throws Exception {
        Trend trend = trendRepository.findById(trend_id);
        TrendInfo trendInfo = new TrendInfo(trend);
        // Get text of tweets
        List<String> tweetsText = tweetsService.getTweets(trend.getName(), 20);
        // Create a list of tweets with their text and FINAL fake score
        List<Tweet> tweets = new ArrayList<>();

        // Create a vector for the tweets using Watson's ToneAnalyzer
        ToneVectorizer toneVectorizer = new ToneVectorizer();
        File tweetsVectors = toneVectorizer.vectorize(tweetsText);
        // Give tweets vectors to Weka's Random Forest model for classification
        RFClassifier classifier = new RFClassifier();
        List<Tweet> rfTweets = classifier.classify(tweetsText, tweetsVectors);

        // Classify using Watson's trained Natural Language Classifier
        NLClassifier nlClassifier = new NLClassifier();
        List<Tweet> nlcTweets = nlClassifier.classify(tweetsText);

        // Get the average of the scores
        for (int i = 0; i < tweetsText.size(); i++) {
            double avgScore = nlcTweets.get(i).getFakeScore();
            if (rfTweets.size() > 0)
                avgScore = (rfTweets.get(i).getFakeScore() + avgScore) / 2.0;
            tweets.add(new Tweet(tweetsText.get(i), avgScore));
        }
        trendInfo.setTweets(tweets);

        //get the x-y chart of the specific trend, defined by its name
        Iterable<Trend> trendsByName = trendRepository.findByNameOrderByIdAsc(trendInfo.getTrend().getName());
        String image = chartService.drawChart(trendsByName);
        trendInfo.setImage(image);

        return trendInfo;
    }

    public Trend save(Trend trend) {
        return trendRepository.save(trend);
    }
}
