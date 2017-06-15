package gr.ntua.repository;

import gr.ntua.domain.Trend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by aris on 13/6/2017.
 */
public interface TrendRepository extends JpaRepository<Trend, Long> {

    List<Trend> findByNameLikeOrderByIdAsc(String name);

    // ATTENTION: Use class names in queries (t.name) instead of table names (t.trend_name)
    // Find all non bursting trends in order to update the bursting flag
    @Query(value = "SELECT DISTINCT t.name FROM Trend t WHERE t.bursting=FALSE")
    List<String> findDistinctNamesNotBursting();

    // Query to update the bursting flag in a trend
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Trend t SET t.bursting=TRUE WHERE t.id = :id")
    void updateBursting(@Param("id") long id);

    // Get all trends by bursting flag
    List<Trend> findAllByBurstingEquals(boolean flag);
}
