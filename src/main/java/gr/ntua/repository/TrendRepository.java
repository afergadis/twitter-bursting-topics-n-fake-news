package gr.ntua.repository;

import gr.ntua.domain.Trend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Temporal;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * Created by aris on 13/6/2017.
 */
public interface TrendRepository extends JpaRepository<Trend, Long> {
    Trend findById(long id);

    Trend findTopByOrderByIdDesc();

    Trend findTopByOrderByIdAsc();

    List<Trend> findByNameOrderByIdAsc(String name);

    List<Trend> findByNameAndIdLessThanEqualOrderByIdAsc(String name, long id);

    List<Trend> findByBurstingGreaterThanEqualAndDateTimeBetween(Double percent,
                                                                 @Temporal(TemporalType.TIMESTAMP) Date from,
                                                                 @Temporal(TemporalType.TIMESTAMP) Date to);

//    List<Trend> findByBurstingGreaterThanEqualOrFirstSeenAndDateTimeBetween(Double percent,
//                                                                             @Temporal(TemporalType.TIMESTAMP) Date from,
//                                                                             @Temporal(TemporalType.TIMESTAMP) Date to);
}
