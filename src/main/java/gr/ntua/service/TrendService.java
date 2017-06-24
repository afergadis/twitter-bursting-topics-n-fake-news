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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aris on 13/6/2017.
 */
@Service
public class TrendService {
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
    public void updateBursting() {
        // Find all new records need to be updated (percent = 0.0)
        List<Trend> trendsToByUpdated = trendRepository.findByBurstingEqualsOrderByIdAsc(0.0);
        for (Trend trendToUpdate : trendsToByUpdated) {
            // Find by name the previous instances of that trend
            List<Trend> trendInstances = trendRepository.findByNameAndIdLessThanEqualOrderByIdAsc(
                    trendToUpdate.getName(), trendToUpdate.getId());
            // First seen trend if there are no previous instances
            if (trendInstances.size() == 1) {
                trendToUpdate.setBursting(100.0);
                trendToUpdate.setFirstSeen(true);
                trendRepository.save(trendToUpdate);
                continue;
            }
            // Get two cascading trends and calculate percent change between previous and current instance.
            for (int i = 0; i < trendInstances.size() - 1; i++) {
                Trend previous = trendInstances.get(i);
                Trend current = trendInstances.get(i + 1);
                if (current.getBursting() > 0.0)
                    continue;
                double percentChange = (current.getVolume() / previous.getVolume().doubleValue() - 1) * 100;
                current.setBursting(percentChange);
                trendRepository.save(current);
            }
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
            dates.add(from);
            dates.add(to);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dates;
    }

    public Iterable<Trend> getBursting(Double percent, Long from, Long to) {
        Trend trend = trendRepository.findTopByOrderByIdDesc();
        if (from != null && from <= 0) {
            from = trend.getTimespanId() + from;
        }
        if (to != null && to <= 0) {
            to = trend.getTimespanId() + to;
        }
        if (from != null && to != null && to < from) {
            return null;
        }
        if (from != null && to == null) {
            return trendRepository.findByBurstingGreaterThanEqualAndTimespanIdGreaterThanEqual(percent, from);
        } else if (from == null && to != null) {
            return trendRepository.findByBurstingGreaterThanEqualAndTimespanIdLessThanEqual(percent, to);
        } else if (from != null) {
            return trendRepository.findByBurstingGreaterThanEqualAndTimespanIdBetween(percent, from, to);
        } else {
            return trendRepository.findByBurstingGreaterThanEqual(percent);
        }
    }

    public Iterable<Trend> getTrendName(String trend_name) {
        return trendRepository.findByNameOrderByIdAsc(trend_name);
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

        // Get the averate of the scores
        for (int i = 0; i < tweetsText.size(); i++) {
            assert rfTweets.get(i).getMessage() == nlcTweets.get(i).getMessage();
            double avgScore = (rfTweets.get(i).getFakeScore() + nlcTweets.get(i).getFakeScore()) / 2.0;
            tweets.add(new Tweet(tweetsText.get(i), avgScore));
        }
        trendInfo.setTweets(tweets);

        //get the x-y chart of the specific trend, defined by its name
        Iterable<Trend> trendsByName = trendRepository.findByNameOrderByIdAsc(trendInfo.getTrend().getName());
        String image = chartService.drawChart(trendsByName);
        trendInfo.setImage(image);

        return trendInfo;
    }
}
