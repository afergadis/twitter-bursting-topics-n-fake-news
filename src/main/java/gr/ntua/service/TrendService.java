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

    /* Finds records that have 0.0 in `is_bursting` column. For every record find the
     * most recent appearance searching by name and sorting by ids descending. If no
     * record is found, then this is a first seen trend.

     */
    // The logic of this function says that two cascading in id trends if they have difference over
    // 'percent' change, then the second indicates a bursting trend.
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
}
