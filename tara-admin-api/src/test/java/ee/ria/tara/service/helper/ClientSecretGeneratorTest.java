package ee.ria.tara.service.helper;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class ClientSecretGeneratorTest {

    private static final Pattern ANY_LOWER_CASE_LETTER = Pattern.compile("[a-z]");
    private static final Pattern ANY_UPPER_CASE_LETTER = Pattern.compile("[A-Z]");
    private static final Pattern ANY_DIGIT = Pattern.compile("[0-9]");
    private static final Pattern ONLY_LETTERS_DIGITS = Pattern.compile("^[a-zA-Z0-9]*$");

    private final ClientSecretGenerator secretGenerator = new ClientSecretGenerator();

    @Test
    void generate_lengthCorrect_onlyContainsAlphanumericSymbols() {
        String secret = secretGenerator.generate();
        
        assertThat(secret).hasSize(ClientSecretGenerator.SIGNING_SECRET_LENGTH);
        assertThat(secret).matches(ONLY_LETTERS_DIGITS);
    }

    /* In rare cases, the generated secret might not always contain every class of allowed symbols.
     * For the test to not be flaky, lets run the test on 2 secrets and check if at least 1 contains all the expected
     * classes of symbols.
     *
     * With `SIGNING_SECRET_LENGTH` of `32`, the test succeeded for 300 000 consecutive runs.
     * If the `SIGNING_SECRET_LENGTH` value is reduced, the number of checked secrets might have to be increased,
     * otherwise the test might get flaky.
     */
    @Test
    void generate_containsLowerCaseLetter_containsUpperCaseLetter_containsDigit() {
        List<String> secrets = IntStream.range(0, 2)
                .mapToObj(ignore -> secretGenerator.generate())
                .toList();

        assertThat(secrets).anySatisfy(secret -> {
            assertThat(secret).containsPattern(ANY_LOWER_CASE_LETTER);
            assertThat(secret).containsPattern(ANY_UPPER_CASE_LETTER);
            assertThat(secret).containsPattern(ANY_DIGIT);
        });

    }
}
