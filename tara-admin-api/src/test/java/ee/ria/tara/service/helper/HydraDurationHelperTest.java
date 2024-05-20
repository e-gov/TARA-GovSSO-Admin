package ee.ria.tara.service.helper;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class HydraDurationHelperTest {
    @Test
    void format_givenHoursMinutesAndSeconds_shouldReturnFormattedString() {
        Duration duration = Duration.ofHours(1).plusMinutes(30).plusSeconds(10);
        assertThat(HydraDurationHelper.format(duration)).isEqualTo("90m10s");
    }

    @Test
    void format_giveMinutesAndSeconds_shouldReturnFormattedString() {
        Duration duration = Duration.ofMinutes(30).plusSeconds(10);
        assertThat(HydraDurationHelper.format(duration)).isEqualTo("30m10s");
    }

    @Test
    void format_givenSeconds_shouldReturnFormattedString() {
        Duration duration = Duration.ofSeconds(10);
        assertThat(HydraDurationHelper.format(duration)).isEqualTo("10s");
    }

    @Test
    void toDuration_givenSeconds_shouldReturnDuration() {
        Duration duration = HydraDurationHelper.toDuration("900s");
        assertThat(duration.toSeconds()).isEqualTo(900);
    }

    @Test
    void toDuration_givenMinutes_shouldReturnDuration() {
        Duration duration = HydraDurationHelper.toDuration("15m");
        assertThat(duration.toMinutes()).isEqualTo(15);
    }

    @Test
    void toDuration_givenMinutesAndSeconds_shouldReturnDuration() {
        Duration duration = HydraDurationHelper.toDuration("5m2s");
        assertThat(duration.toSeconds()).isEqualTo(302);
    }
}
