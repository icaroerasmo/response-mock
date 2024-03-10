package com.icaroerasmo.responsemock.utils;

import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TimeUtil {

    private static final int SECOND = 1000;
    private static final int MINUTE = SECOND * 60;
    private static final int HOUR = MINUTE * 60;

    public Duration parseDurationFromString(String durationStr) {
        final Pattern p = Pattern.compile("(\\d+)(ms|[hms])");
        final Matcher m = p.matcher(durationStr);
        long totalMillis = 0;
        while (m.find()) {
            final int duration = Integer.parseInt(m.group(1));
            final TimeUnit interval = toTimeUnit(m.group(2));
            final long l = interval.toMillis(duration);
            totalMillis = totalMillis + l;
            System.out.format("TimeUnit.%s.toMillis(%s) = %d\n", interval.name(), duration, l);
        }
        return Duration.of(totalMillis, ChronoUnit.MILLIS);
    }

    public String durationToString(Duration duration) {
        long totalDuration = duration.toMillis();
        long seconds = 0, minutes = 0, hours = 0, milliseconds = 0;
        while(totalDuration > 0) {
            if(totalDuration >= HOUR) {
                totalDuration -= HOUR;
                hours++;
            } else if(totalDuration >= MINUTE) {
                totalDuration -= MINUTE;
                minutes++;
            } else if(totalDuration >= SECOND) {
                totalDuration -= SECOND;
                seconds++;
            } else {
                milliseconds = totalDuration;
                totalDuration = 0;
            }
        }

        StringBuilder builder = new StringBuilder();

        if(hours > 0) {
            builder.append("%sh".formatted(hours));
        }
        if(minutes > 0) {
            builder.append("%sm".formatted(minutes));
        }
        if(seconds > 0) {
            builder.append("%ss".formatted(seconds));
        }
        if(milliseconds > 0) {
            builder.append("%sms".formatted(milliseconds));
        }
        return builder.toString();
    }

    private TimeUnit toTimeUnit(final String unit) {
        switch (unit) {
            case "ms": return TimeUnit.MILLISECONDS;
            case "s": return TimeUnit.SECONDS;
            case "m": return TimeUnit.MINUTES;
            case "h": return TimeUnit.HOURS;
            default: throw new IllegalArgumentException(String.format("%s is not a valid code [ms,s,m,h]", unit));
        }
    }
}
