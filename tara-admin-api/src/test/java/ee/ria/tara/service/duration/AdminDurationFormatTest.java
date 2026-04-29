package ee.ria.tara.service.duration;

import ee.ria.tara.duration.AdminDurationFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminDurationFormatTest {

    private final AdminDurationFormat adminDurationFormat = new AdminDurationFormat();

    @SuppressWarnings("unused") // Used by `@FieldSource("FORMAT_TEST_CASES")`
    private static final List<FormatTestCase> FORMAT_TEST_CASES = List.of(
            new FormatTestCase("Seconds", Duration.ofSeconds(30), "30s"),
            new FormatTestCase("Minutes", Duration.ofMinutes(15), "15m"),
            new FormatTestCase("Hours", Duration.ofHours(1), "1h"),
            new FormatTestCase("Days", Duration.ofDays(90), "90d"),
            new FormatTestCase("Days, hours, minutes, seconds",
                    Duration.ofDays(1).plusHours(2).plusMinutes(3).plusSeconds(4), "1d2h3m4s")
    );

    @SuppressWarnings("unused") //Used by `@FieldSource("PARSE_TEST_CASES")`
    private static final List<ParseTestCase> PARSE_TEST_CASES = List.of(
            new ParseTestCase("Seconds", "30s", Duration.ofSeconds(30)),
            new ParseTestCase("Minutes, seconds as seconds", "90s", Duration.ofSeconds(90)),
            new ParseTestCase("Minutes", "15m", Duration.ofMinutes(15)),
            new ParseTestCase("Hours, minutes as minutes", "90m", Duration.ofMinutes(90)),
            new ParseTestCase("Hours", "1h", Duration.ofHours(1)),
            new ParseTestCase("Days, hours as hours", "90h", Duration.ofHours(90)),
            new ParseTestCase("Days, hours, minutes, seconds",
                    "1d2h3m4s", Duration.ofDays(1).plusHours(2).plusMinutes(3).plusSeconds(4))
    );


    @ParameterizedTest(name = "{0}")
    @FieldSource("FORMAT_TEST_CASES")
    void format_givenValidInput_returnsFormattedValue(FormatTestCase testCase) {
        String actual = adminDurationFormat.format(testCase.input());
        String expected = testCase.output();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void format_givenNull_throwsException() {
        //noinspection DataFlowIssue
        assertThatThrownBy(() -> adminDurationFormat.format(null))
                .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest(name = "{0}")
    @FieldSource("PARSE_TEST_CASES")
    void parse_givenValidInput_returnsDuration(ParseTestCase testCase) {
        Duration actual = adminDurationFormat.parse(testCase.input());
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
        assertThatThrownBy(() -> adminDurationFormat.parse(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse_givenNull_throwsException() {
        //noinspection DataFlowIssue
        assertThatThrownBy(() -> adminDurationFormat.parse(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void roundTrip_displayFormat_shouldBeStable() {
        String original = "89d1h30m";
        Duration duration = adminDurationFormat.parse(original);
        assertThat(adminDurationFormat.format(duration)).isEqualTo(original);
    }
}
