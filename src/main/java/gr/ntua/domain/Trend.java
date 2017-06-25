package gr.ntua.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by aris on 13/6/2017.
 */
@Entity
@Table(name = "trends_new")
public class Trend {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "timespan_id")
    private Long timespanId; // TODO: Remove
    @Column(name = "trend_name") // TODO: Rename
    private String name;
    @Column(name = "trend_volume") // TODO: Rename
    private Integer volume;
    @Column(name = "is_bursting") // TODO: Rename
    private Double bursting;
    @Column(name = "first_seen")
    private boolean firstSeen;
    @Column(name = "date_time", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    public Trend(Long timespanId, String name, Integer volume) {
        this.timespanId = timespanId;
        this.name = name;
        this.volume = volume;
        bursting = 0.0;
        firstSeen = false;
        dateTime = new Date();
    }

    public Trend() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getBursting() {
        return bursting;
    }

    public void setBursting(Double bursting) {
        this.bursting = bursting;
    }

    public boolean isFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(boolean firstSeen) {
        this.firstSeen = firstSeen;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Trend{" +
                "id=" + id +
                ", timespanId=" + timespanId +
                ", name='" + name + '\'' +
                ", volume=" + volume +
                ", bursting=" + bursting +
                ", firstSeen=" + firstSeen +
                ", dateTime=" + dateTime +
                '}';
    }

}
