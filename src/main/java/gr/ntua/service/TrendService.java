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
    public void updateBursting(double percent) {
        List<String> distinctByName = trendRepository.findDistinctNamesNotBursting();
        for (String name : distinctByName) {
            List<Trend> trendName = trendRepository.findByNameLikeOrderByIdAsc(name);
            // Update two cascading trend_name instances if the volume is
            // greater than the percent.
            for (int i = 0; i < trendName.size() - 1; i++) {
                Trend t1 = trendName.get(i);
                Trend t2 = trendName.get(i + 1);
                double percentChange = (t2.getVolume() / t1.getVolume().doubleValue() - 1) * 100;
                if (percentChange >= percent) {
                    t2.setBursting(true);
                    trendRepository.save(t2);
                }
            }
        }
    }

    public Iterable<Trend> getBursting() {
        return trendRepository.findAllByBurstingEquals(true);
    }
}
