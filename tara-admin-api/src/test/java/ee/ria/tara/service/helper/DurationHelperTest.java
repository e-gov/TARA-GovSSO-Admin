package ee.ria.tara.service.helper;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DurationHelperTest {

    @Test
    void toDuration_givenSeconds_shouldReturnDuration() {
        assertThat(DurationHelper.toDuration("900s")).isEqualTo(Duration.ofSeconds(900));
    }

    @Test
    void toDuration_givenMinutes_shouldReturnDuration() {
        assertThat(DurationHelper.toDuration("15m")).isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    void toDuration_givenHours_shouldReturnDuration() {
        assertThat(DurationHelper.toDuration("2160h")).isEqualTo(Duration.ofDays(90));
    }

    @Test
    void toDuration_givenDays_shouldReturnDuration() {
        assertThat(DurationHelper.toDuration("90d")).isEqualTo(Duration.ofDays(90));
    }

    @Test
    void toDuration_givenDaysHoursMinutesAndSeconds_shouldReturnDuration() {
        assertThat(DurationHelper.toDuration("1d2h3m4s"))
                .isEqualTo(Duration.ofDays(1).plusHours(2).plusMinutes(3).plusSeconds(4));
    }

    @Test
    void toDuration_givenEmptyString_shouldThrowException() {
        assertThatThrownBy(() -> DurationHelper.toDuration(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void toDuration_givenInvalidFormat_shouldThrowException() {
        assertThatThrownBy(() -> DurationHelper.toDuration("abc"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void toDuration_givenNumberWithoutUnit_shouldThrowException() {
        assertThatThrownBy(() -> DurationHelper.toDuration("900"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void toDisplayString_givenSeconds_shouldReturnFormattedString() {
        assertThat(DurationHelper.toDisplayString(Duration.ofSeconds(10))).isEqualTo("10s");
    }

    @Test
    void toDisplayString_givenMinutesAndSeconds_shouldReturnFormattedString() {
        assertThat(DurationHelper.toDisplayString(Duration.ofMinutes(5).plusSeconds(2))).isEqualTo("5m2s");
    }

    @Test
    void toDisplayString_givenHoursAndMinutes_shouldReturnFormattedString() {
        assertThat(DurationHelper.toDisplayString(Duration.ofHours(1).plusMinutes(30))).isEqualTo("1h30m");
    }

    @Test
    void toDisplayString_givenDays_shouldReturnFormattedString() {
        assertThat(DurationHelper.toDisplayString(Duration.ofDays(90))).isEqualTo("90d");
    }

    @Test
    void toDisplayString_givenDaysAndHours_shouldReturnFormattedString() {
        assertThat(DurationHelper.toDisplayString(Duration.ofDays(89).plusHours(1))).isEqualTo("89d1h");
    }

    @Test
    void roundTrip_displayFormat_shouldBeStable() {
        String original = "89d1h30m";
        Duration duration = DurationHelper.toDuration(original);
        assertThat(DurationHelper.toDisplayString(duration)).isEqualTo(original);
    }
}
