package dev.rosewood.roseloot.util;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtils {

    private static final Pattern ENTIRE_DURATION_PATTERN = Pattern.compile("((\\d+)(ms|[smhd]))+");
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)(ms|[smhd])");

    private TimeUtils() {

    }

    /**
     * Gets the duration of a time string
     *
     * @param timeString The time string
     * @return The duration in milliseconds
     */
    public static long getDuration(String timeString) {
        if (!ENTIRE_DURATION_PATTERN.matcher(timeString).matches())
            return -1;

        long duration = 0;
        Matcher matcher = DURATION_PATTERN.matcher(timeString);
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String suffix = matcher.group(2);
            switch (suffix.toLowerCase()) {
                case "ms" -> duration += value;
                case "s" -> duration += TimeUnit.SECONDS.toMillis(value);
                case "m" -> duration += TimeUnit.MINUTES.toMillis(value);
                case "h" -> duration += TimeUnit.HOURS.toMillis(value);
                case "d" -> duration += TimeUnit.DAYS.toMillis(value);
                case "y" -> duration += TimeUnit.DAYS.toMillis(value) * 365;
                default -> {
                    return -1;
                }
            }
        }

        return duration;
    }

    /**
     * Gets the time string until a certain time
     *
     * @param target The target time
     * @return The time string or null if the target time is in the past
     */
    public static String getTimeStringUntil(long target) {
        long difference = target - System.currentTimeMillis();
        if (difference <= 0)
            return null;

        StringBuilder builder = new StringBuilder();
        if (difference >= TimeUnit.DAYS.toMillis(365)) {
            long years = TimeUnit.DAYS.toDays(difference) / 365;
            builder.append(years).append("y");
            difference -= TimeUnit.DAYS.toMillis(years * 365);
        }

        if (difference >= TimeUnit.DAYS.toMillis(1)) {
            long days = TimeUnit.MILLISECONDS.toDays(difference);
            builder.append(days).append("d");
            difference -= TimeUnit.DAYS.toMillis(days);
        }

        if (difference >= TimeUnit.HOURS.toMillis(1)) {
            long hours = TimeUnit.MILLISECONDS.toHours(difference);
            builder.append(hours).append("h");
            difference -= TimeUnit.HOURS.toMillis(hours);
        }

        if (difference >= TimeUnit.MINUTES.toMillis(1)) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
            builder.append(minutes).append("m");
            difference -= TimeUnit.MINUTES.toMillis(minutes);
        }

        if (difference >= TimeUnit.SECONDS.toMillis(1)) {
            long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);
            builder.append(seconds).append("s");
            difference -= TimeUnit.SECONDS.toMillis(seconds);
        }

        if (difference > 0) {
            builder.append(difference).append("ms");
        }

        return builder.toString();
    }

}
