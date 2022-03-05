package com.amitthk.sbamqpconcurrency.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class TimeRange {
    final LocalDateTime from;
    final LocalDateTime to;
    public Duration getDuration(){
        return Duration.between(from,to);
    }
}
