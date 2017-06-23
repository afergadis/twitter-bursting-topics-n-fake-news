package gr.ntua.service;

import gr.ntua.domain.Trend;
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
        if (from != null && from <= 0) {
            Trend trend = trendRepository.findTopByOrderByIdDesc();
            from = trend.getTimespanId() + from;
        }
        if (to != null && to <= 0) {
            return null;
        }
        if (from != null && to == null) {
            return trendRepository.findByBurstingGreaterThanEqualAndTimespanIdGreaterThanEqual(percent, from);
        } else if (from == null && to != null) {
//            return trendRepository.findByBurstingGreaterThanEqualAndTimespanIdLessThanEqualOrderByIdAsc(percent, to);
            return trendRepository.findByBurstingGreaterThanEqualAndTimespanIdGreaterThanEqual(percent, from);
        } else if (from != null) {
            return trendRepository.findByBurstingGreaterThanEqualAndTimespanIdBetween(percent, from, to);
        } else {
            return trendRepository.findByBurstingGreaterThanEqual(percent);
        }
    }

    public Iterable<Trend> getTrendName(String trend_name) {
        return trendRepository.findByNameOrderByIdAsc(trend_name);
    }
}
