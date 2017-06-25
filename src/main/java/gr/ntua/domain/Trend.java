package gr.ntua.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by aris on 13/6/2017.
 */
@Entity
@Table(name = "trends")
public class Trend {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "volume")
    private Integer volume;
    @Column(name = "burstiness")
    private Double bursting;
    @Column(name = "first_seen")
    private boolean firstSeen;
    @Column(name = "date_time", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    public Trend(String name, Integer volume) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVolume() {
        return volume;
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

    public void setFirstSeen() {
        this.firstSeen = true;
    }

    public Date getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "Trend{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", volume=" + volume +
                ", burstiness=" + bursting +
                ", firstSeen=" + firstSeen +
                ", dateTime=" + dateTime +
                '}';
    }

}
