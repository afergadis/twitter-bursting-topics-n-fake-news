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

    List<Trend> findByNameAndBurstingGreaterThanEqual(String name, double percent);

    List<Trend> findByNameOrderByIdAsc(String name);

    List<Trend> findByNameAndIdLessThanEqualOrderByIdAsc(String name, long id);

    List<Trend> findByBurstingEqualsOrderByIdAsc(Double percent);

    List<Trend> findByBurstingGreaterThanEqual(Double percent);

    List<Trend> findByBurstingGreaterThanEqualAndTimespanIdBetween(Double percent, Long start, Long end);

    List<Trend> findByBurstingGreaterThanEqualAndTimespanIdGreaterThanEqual(Double percent, Long from);

    List<Trend> findByBurstingGreaterThanEqualAndTimespanIdLessThanEqual(Double percent, Long to);

    Trend findById(long id);

    Trend findTopByOrderByIdDesc();

    Trend findTopByOrderByIdAsc();

    List<Trend> findByBurstingGreaterThanEqualAndDateTimeBetween(Double percent,
                                                                 @Temporal(TemporalType.TIMESTAMP) Date from,
                                                                 @Temporal(TemporalType.TIMESTAMP) Date to);
}
