package com.amitthk.sbamqpconcurrency.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

//@JsonIgnoreProperties({"astNode", "parent"})
@Data
public class TimeRangeList implements Iterable<TimeRange>{
    private String partitionColumn;
    private LocalDateTime toDate;
    private LocalDateTime fromDate;
    private List<TimeRange> timeRangeList;

    @Override
    public Iterator<TimeRange> iterator() {
        return timeRangeList.iterator();
    }

    public static class TimeRangeBuilder {
        private final TimeRangeList range;

        public TimeRangeBuilder() {
            this.range = new TimeRangeList();
        }
        public TimeRangeList build(LocalDateTime fromDate, LocalDateTime toDate, Duration rangeDuration) {
            final TimeRangeList range = new TimeRangeList();
            range.timeRangeList = new LinkedList<>();

            LocalDateTime startPoint = fromDate;
            LocalDateTime nextPoint = fromDate.plusSeconds(rangeDuration.getSeconds());
            while (nextPoint.isBefore(toDate)){
                range.appendRange(new TimeRange(startPoint,nextPoint));
                startPoint=nextPoint;
                nextPoint=nextPoint.plusSeconds(rangeDuration.getSeconds());
            }
        }
    }

    private void appendRange(TimeRange range) {
        timeRangeList.add(range);
    }

    public static TimeRangeBuilder getBuilder() {
        return new TimeRangeBuilder();
    }
}