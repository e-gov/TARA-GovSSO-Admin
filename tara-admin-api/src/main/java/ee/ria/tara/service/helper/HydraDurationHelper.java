package ee.ria.tara.service.helper;

import lombok.NonNull;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

public class HydraDurationHelper {
    private static final String DURATION_UNIT_PATTERN = "([0-9]+)(s|m)";

    public static String format(@NonNull Duration duration) {
        return formatDuration(duration.toMillis(), "m'm's's'").replaceAll("(?<!\\d)0[ms]", "");
    }

    public static Duration toDuration(@NonNull String durationString) {
        Pattern pattern = Pattern.compile(DURATION_UNIT_PATTERN);
        Matcher matcher = pattern.matcher(durationString);
        Duration duration = Duration.ofSeconds(0);
        while (matcher.find()) {
            long amount = Long.parseLong(matcher.group(1));
            String timeUnit = matcher.group(2);
            duration = updateDuration(duration, timeUnit, amount);
        }
        return duration;
    }

    private static Duration updateDuration(Duration duration, String timeUnit, long amount) {
        switch (timeUnit) {
            case "m":
                return duration.plusMinutes(amount);
            case "s":
                return duration.plusSeconds(amount);
            default:
                return duration;
        }
    }
}
