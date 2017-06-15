package gr.ntua.repository;

import gr.ntua.domain.Trend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by aris on 13/6/2017.
 */
public interface TrendRepository extends JpaRepository<Trend, Long> {

    List<Trend> findByNameAndBurstingGreaterThanEqual(String name, double percent);

    List<Trend> findByNameOrderByIdAsc(String name);

    List<Trend> findByNameAndTimespanId(String name, long timespanId);

    List<Trend> findByBurstingEquals(Double percent);

    List<Trend> findByBurstingGreaterThanEqual(Double percent);


    // ATTENTION: Use class names in queries (t.name) instead of table names (t.trend_name)
    // Find all trend names that has bursting value zero. This new are entries since
    // the last api call to get bursting trends.
//    @Query(value = "SELECT DISTINCT t.name FROM Trend t WHERE t.bursting=0")
//    List<String> findDistinctNamesAndBurstingEqualsZero();
}
