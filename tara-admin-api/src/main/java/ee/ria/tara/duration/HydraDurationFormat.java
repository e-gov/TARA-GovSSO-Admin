package ee.ria.tara.duration;

import lombok.NonNull;

import java.time.Duration;

public class HydraDurationFormat implements DurationFormat {

    private static final DurationFormat ADMIN_DURATION_FORMAT = new AdminDurationFormat();

    public Duration parse(@NonNull String hydraDuration) {
        return ADMIN_DURATION_FORMAT.parse(hydraDuration);
    }

    /**
     * Formats a java Duration into a Hydra-compatible string using h/m/s units
     * (e.g. "2160h" for 90 days, "15m" for 15 minutes). Days are omitted because
     * Hydra does not support the "d" unit.
     */
    @SuppressWarnings("DuplicatedCode")
    public String format(@NonNull Duration duration) {
        if (duration.compareTo(Duration.ofSeconds(1)) < 0) {
            return "0s";
        }
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("h");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m");
        }
        if (seconds > 0) {
            sb.append(seconds).append("s");
        }
        return sb.toString();
    }
}
