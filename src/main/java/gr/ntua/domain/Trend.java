package gr.ntua.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by aris on 13/6/2017.
 */
@Entity
@Table(name = "trends_backup")
public class Trend {
    @Id
    @Column(name = "timespan_id")
    private Long id;
    @Column(name = "trend_name")
    private String name;
    @Column(name = "trend_volume")
    private Integer volume;
    @Column(name = "is_bursting")
    private String isBursting;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public String isBursting() {
        return isBursting;
    }

    public void setBursting(String bursting) {
        isBursting = bursting;
    }
}
