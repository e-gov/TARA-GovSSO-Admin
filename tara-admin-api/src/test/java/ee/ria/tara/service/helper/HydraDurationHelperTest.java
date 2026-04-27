package ee.ria.tara.service.helper;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class HydraDurationHelperTest {

    @Test
    void toHydraString_givenMinutes_shouldReturnFormattedString() {
        assertThat(HydraDurationHelper.toHydraString(Duration.ofMinutes(15))).isEqualTo("15m");
    }

    @Test
    void toHydraString_givenHoursAndMinutes_shouldReturnFormattedString() {
        assertThat(HydraDurationHelper.toHydraString(Duration.ofHours(1).plusMinutes(30))).isEqualTo("1h30m");
    }

    @Test
    void toHydraString_givenDays_shouldReturnHoursNotDays() {
        assertThat(HydraDurationHelper.toHydraString(Duration.ofDays(90))).isEqualTo("2160h");
    }

    @Test
    void toHydraString_givenDaysAndHours_shouldReturnHoursNotDays() {
        assertThat(HydraDurationHelper.toHydraString(Duration.ofDays(89).plusHours(1))).isEqualTo("2137h");
    }

    @Test
    void toDuration_givenHydraString_shouldReturnDuration() {
        assertThat(HydraDurationHelper.toDuration("2160h")).isEqualTo(Duration.ofDays(90));
    }

    @Test
    void roundTrip_hydraFormat_shouldBeStable() {
        String original = "2137h";
        Duration duration = HydraDurationHelper.toDuration(original);
        assertThat(HydraDurationHelper.toHydraString(duration)).isEqualTo(original);
    }
}
