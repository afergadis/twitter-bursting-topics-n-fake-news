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
    @Autowired
    private TrendRepository trendRepository;

    public void updateBursting(double percent) {
        List<Trend> distinctByName = trendRepository.findDistinctByNameLike("%");
        for (Trend trend : distinctByName) {
            List<Trend> trendName = trendRepository.findByNameOrderByIdAsc(trend.getName());
            // Update two cascading trend_name instances if the volume is
            // greater than the percent.
            for (int i = 0; i < trendName.size() - 1; i++) {
                Trend t1 = trendName.get(i);
                Trend t2 = trendName.get(i + 1);
                double percentChange = (t2.getVolume() / t1.getVolume() - 1) * 100;
                if (percentChange >= percent)
                    t2.setBursting("1");
            }
        }
    }

    public Iterable<Trend> getBursting() {
        return trendRepository.findAllByIsBurstingEquals("1");
    }
}
