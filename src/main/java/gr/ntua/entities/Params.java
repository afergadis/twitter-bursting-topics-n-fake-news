package gr.ntua.entities;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class Params {
    private Double percent;
    private String from = null;
    private String to = null;

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List getPossibleFrom() {
        DateTime dt = new DateTime();  // current time
        int hours = dt.getHourOfDay(); // gets hour of day

        List fromList = new ArrayList<String>();
        fromList.add("From Time ...");

        if ((hours % 2) != 0) {
            hours = hours - 1;
        }
        for (int i = hours; i <= 24; i = i + 2) {
            fromList.add(i + ":00");
        }

        for (int i = 2; i < hours; i = i + 2) {
            fromList.add(i + ":00");
        }

        return fromList;
    }

    public List getPossibleTo() {
        DateTime dt = new DateTime();  // current time
        int hours = dt.getHourOfDay(); // gets hour of day

        List toList = new ArrayList<String>();
        toList.add("Until Time ...");

        if ((hours % 2) != 0) {
            hours = hours - 1;
        }

        for (int i = hours + 2; i <= 24; i = i + 2) {
            toList.add(i + ":00");
        }

        for (int i = 2; i <= hours; i = i + 2) {
            toList.add(i + ":00");
        }

        return toList;
    }

    public Long convert2timespan(String time) throws Exception {
        if (time.contains(":")) {
            String[] parts = time.split(":");
            int chosenTime = Integer.parseInt(parts[0]);

            DateTime dt = new DateTime();  // current time
            int hours = dt.getHourOfDay();
            int timespan = 0;

            if (hours > chosenTime) {
                timespan = -1 * (hours - chosenTime);
            } else {
                timespan = -1 * (24 - (chosenTime - hours));
            }

            return new Long(timespan);
        } else {
            throw new Exception();
        }
    }
}
