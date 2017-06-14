package gr.ntua.repository;

import gr.ntua.domain.Trend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by aris on 13/6/2017.
 */
public interface TrendRepository extends JpaRepository<Trend, Long> {

    //    List<Trend> findByNameLike(String name);
    List<Trend> findByNameOrderByIdAsc(String name);

    List<Trend> findDistinctByNameLike(String name);

    //    @Query(value = "SELECT t.trend_name FROM Trend t")
//    List<Trend> findDistinctNames();
    List<Trend> findAllByIsBurstingEquals(String num);
}
