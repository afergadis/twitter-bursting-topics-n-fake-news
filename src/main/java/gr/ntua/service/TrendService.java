package gr.ntua.service;

import gr.ntua.domain.Trend;
import gr.ntua.domain.TrendInfo;
import gr.ntua.domain.Tweet;
import gr.ntua.repository.TrendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by aris on 13/6/2017.
 */
@Service
public class TrendService {
    private final TrendRepository trendRepository;

    @Autowired
    public TrendService(TrendRepository trendRepository) {
        this.trendRepository = trendRepository;
    }

    // The logic of this function says that two cascading in id trends if they have difference over
    // 'percent' change, then the second indicates a bursting trend.
    // Q1: To we save the 'bursting' flag in database?
    //     If we do, then if the use changes the percent?
    public void updateBursting() {
        List<Trend> trends = trendRepository.findByBurstingEquals(0.0);
        for (Trend trend : trends) {
            // Get the previous instance(s?) of that trend
            List<Trend> byNameAndTimespanId = trendRepository.findByNameAndTimespanId(trend.getName(), trend.getTimespanId() - 1);
            if (byNameAndTimespanId.size() == 0) {
                // First seen trend
                trend.setBursting(100.0);
                trendRepository.save(trend);
                continue;
            }
            // Update two cascading trend_name instances if the volume is greater than the percent.
            for (Trend t : byNameAndTimespanId) {
                double percentChange = (trend.getVolume() / t.getVolume().doubleValue() - 1) * 100;
                trend.setBursting(percentChange);
                trendRepository.save(trend);
            }
        }
    }

    public Iterable<Trend> getBursting(Double percent, Long from, Long to) {
        // Treat to==null and to==0 as now
        if (to == null || to == 0) {
            Trend trend = trendRepository.findTopByOrderByIdDesc();
            to = trend.getTimespanId();
        }
        if (to < 0) { // Find the relative `to`
            Trend trend = trendRepository.findTopByOrderByIdDesc();
            to = trend.getTimespanId() + to;
        }
        if (from == null) {  // From the beginning until `to`
            return trendRepository.findByBurstingGreaterThanEqualAndTimespanIdLessThanEqual(percent, to);
        }
        if (from < to)  // Should be, either negative or positive
            return trendRepository.findByBurstingGreaterThanEqualAndTimespanIdBetween(percent, from, to);
        return null;
    }

    public Iterable<Trend> getTrendName(String trend_name) {
        return trendRepository.findByNameOrderByIdAsc(trend_name);
    }

    //TODO: should return the trend info and the tweets
    public TrendInfo getTrendInfo(Long trend_id) {
        //TODO: get the Trend with the specific id and initialize the TrendInfo
        //dummy
        Trend dummyTrend = new Trend();
        dummyTrend.setId(trend_id);
        dummyTrend.setName("Topic_1");

        //TODO: Go to TrendInfo and change the dummy ...
        TrendInfo trendInfo = new TrendInfo(dummyTrend);

        //dummy for tweets
        for(int i=0; i<5; i++) {
            Tweet t = new Tweet();
            t.setMessage("hello. this is a tweet message");
            t.setFakeScore(i*0.25*100);
            trendInfo.addTweet(t);
        }

        return trendInfo;
    }
}
