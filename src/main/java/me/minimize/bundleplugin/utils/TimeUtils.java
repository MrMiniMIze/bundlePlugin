package me.minimize.bundleplugin.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {

    /**
     * Parses a date/time string like "2025-02-01 09:00:00"
     * in America/Los_Angeles. If blank or invalid:
     *  - if useNowIfMissing = true, returns current time
     *  - else, returns Long.MAX_VALUE
     */
    public static long parseDateTimeInPST(String dateTimeStr, boolean useNowIfMissing) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return useNowIfMissing ? System.currentTimeMillis() : Long.MAX_VALUE;
        }
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime ldt = LocalDateTime.parse(dateTimeStr, fmt);
            ZoneId zone = ZoneId.of("America/Los_Angeles");
            ZonedDateTime zdt = ldt.atZone(zone);
            return zdt.toInstant().toEpochMilli();
        } catch (Exception e) {
            return useNowIfMissing ? System.currentTimeMillis() : Long.MAX_VALUE;
        }
    }

    /**
     * Formats remaining milliseconds into "xd yh zm ws".
     */
    public static String formatDuration(long millis) {
        if (millis == Long.MAX_VALUE) {
            return "∞";
        }
        long diff = millis;
        if (diff <= 0) {
            return "0s";
        }

        long days = TimeUnit.MILLISECONDS.toDays(diff);
        diff -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        diff -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        diff -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);

        StringBuilder sb = new StringBuilder();
        if (days > 0)   sb.append(days).append("d ");
        if (hours > 0)  sb.append(hours).append("h ");
        if (minutes > 0)sb.append(minutes).append("m ");
        if (seconds > 0)sb.append(seconds).append("s");

        return sb.toString().trim();
    }
}
