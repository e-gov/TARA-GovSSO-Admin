package ee.ria.tara.service.duration;

import ee.ria.tara.duration.HydraDurationFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HydraDurationFormatTest {

    private final HydraDurationFormat hydraDurationFormat = new HydraDurationFormat();

    @SuppressWarnings("unused") // Used by `@FieldSource("FORMAT_TEST_CASES")`
    private static final List<FormatTestCase> FORMAT_TEST_CASES = List.of(
            new FormatTestCase("Seconds", Duration.ofSeconds(30), "30s"),
            new FormatTestCase("Minutes", Duration.ofMinutes(15), "15m"),
            new FormatTestCase("Hours", Duration.ofHours(1), "1h"),
            new FormatTestCase("Hours, minutes, seconds", Duration.ofHours(1).plusMinutes(2).plusSeconds(3), "1h2m3s"),
            new FormatTestCase("Days to hours", Duration.ofDays(90), "2160h"),
            new FormatTestCase("Days, hours to just hours", Duration.ofDays(90).plusHours(1), "2161h")
    );

    @SuppressWarnings("unused") //Used by `@FieldSource("PARSE_TEST_CASES")`
    private static final List<ParseTestCase> PARSE_TEST_CASES = List.of(
            new ParseTestCase("Seconds", "30s", Duration.ofSeconds(30)),
            new ParseTestCase("Minutes, seconds as seconds", "90s", Duration.ofSeconds(90)),
            new ParseTestCase("Minutes", "15m", Duration.ofMinutes(15)),
            new ParseTestCase("Hours, minutes as minutes", "90m", Duration.ofMinutes(90)),
            new ParseTestCase("Hours", "1h", Duration.ofHours(1)),
            new ParseTestCase("Days as hours", "2160h", Duration.ofDays(90)),
            new ParseTestCase("Days, hours as hours", "90h", Duration.ofHours(90)),
            new ParseTestCase("Hours, minutes, seconds", "1h2m3s", Duration.ofHours(1).plusMinutes(2).plusSeconds(3))
    );

    @ParameterizedTest(name = "{0}")
    @FieldSource("FORMAT_TEST_CASES")
    void format_givenValidInput_returnsFormattedValue(FormatTestCase testCase) {
        String actual = hydraDurationFormat.format(testCase.input());
        String expected = testCase.output();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void format_givenNull_throwsException() {
        //noinspection DataFlowIssue
        assertThatThrownBy(() -> hydraDurationFormat.format(null))
                .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest(name = "{0}")
    @FieldSource("PARSE_TEST_CASES")
    void parse_givenValidInput_returnsDuration(ParseTestCase testCase) {
        Duration actual = hydraDurationFormat.parse(testCase.input());
        Duration expected = testCase.output();
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "abc",
            "900",
            "0.5s",
            "1ms"
    })
    void parse_givenInvalidInput_throwsException(String input) {
        assertThatThrownBy(() -> hydraDurationFormat.parse(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse_givenNull_throwsException() {
        //noinspection DataFlowIssue
        assertThatThrownBy(() -> hydraDurationFormat.parse(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void roundTrip_hydraFormat_shouldBeStable() {
        String original = "2137h20m10s";
        Duration duration = hydraDurationFormat.parse(original);
        assertThat(hydraDurationFormat.format(duration)).isEqualTo(original);
    }

}
