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
    @Column(name = "id")
    private Long id;
    @Column(name = "timespan_id")
    private Long timespanId;
    @Column(name = "trend_name")
    private String name;
    @Column(name = "trend_volume")
    private Integer volume;
    @Column(name = "is_bursting")
    private boolean bursting;

    public Long getId() {
        return id;
    }

    public Long getTimespanId() {
        return timespanId;
    }

    public void setTimespanId(Long timespanId) {
        this.timespanId = timespanId;
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

    public boolean getBursting() {
        return bursting;
    }

    public void setBursting(boolean bursting) {
        this.bursting = bursting;
    }

    @Override
    public String toString() {
        return "Trend{" +
                "timespanId=" + timespanId +
                ", name='" + name + '\'' +
                ", volume=" + volume +
                ", bursting='" + bursting + '\'' +
                '}';
    }
}
