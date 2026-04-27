package ee.ria.tara.service.helper;

import lombok.NonNull;

import java.time.Duration;

public class HydraDurationHelper {

    public static Duration toDuration(@NonNull String hydraDuration) {
        return DurationHelper.toDuration(hydraDuration);
    }

    /**
     * Formats a java Duration into a Hydra-compatible string using h/m/s units
     * (e.g. "2160h" for 90 days, "15m" for 15 minutes). Days are omitted because
     * Hydra does not support the "d" unit.
     */
    public static String toHydraString(@NonNull Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h");
        if (minutes > 0) sb.append(minutes).append("m");
        if (seconds > 0) sb.append(seconds).append("s");
        return sb.toString();
    }
}
