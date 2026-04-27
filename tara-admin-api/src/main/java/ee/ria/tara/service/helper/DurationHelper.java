package ee.ria.tara.service.helper;

import lombok.NonNull;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationHelper {
    private static final String DURATION_UNIT_PATTERN = "([0-9]+)(d|h|m|s)";
    private static final Pattern VALID_FORMAT = Pattern.compile("^(" + DURATION_UNIT_PATTERN + ")+$");

    /**
     * Parses a duration string in d/h/m/s format (e.g. "89d1h30m") or Hydra's h/m/s format
     * (e.g. "2137h") into java Duration. Days are not native to Hydra but are supported
     * as input and display format.
     */
    public static Duration toDuration(@NonNull String durationString) {
        if (!VALID_FORMAT.matcher(durationString).matches()) {
            throw new IllegalArgumentException("Invalid duration format: '" + durationString + "'. Expected format: " + VALID_FORMAT.pattern());
        }
        Matcher matcher = Pattern.compile(DURATION_UNIT_PATTERN).matcher(durationString);
        Duration duration = Duration.ZERO;
        while (matcher.find()) {
            long amount = Long.parseLong(matcher.group(1));
            switch (matcher.group(2)) {
                case "d": duration = duration.plusDays(amount); break;
                case "h": duration = duration.plusHours(amount); break;
                case "m": duration = duration.plusMinutes(amount); break;
                case "s": duration = duration.plusSeconds(amount); break;
            }
        }
        return duration;
    }

    /**
     * Formats a java Duration into display string using d/h/m/s units
     * (e.g. "90d" for 90 days, "89d1h30m" for 89 days 1 hour 30 minutes).
     */
    public static String toDisplayString(@NonNull Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d");
        if (hours > 0) sb.append(hours).append("h");
        if (minutes > 0) sb.append(minutes).append("m");
        if (seconds > 0) sb.append(seconds).append("s");
        return sb.toString();
    }
}
