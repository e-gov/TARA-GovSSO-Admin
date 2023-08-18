package ee.ria.tara.service.helper;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class PaginationHelper {
    public static final String INITIAL_PAGE_TOKEN = "1"; // page_token parameter could be omitted for the first page, but it's simpler to use the default value defined at https://www.ory.sh/docs/hydra/reference/api#tag/oAuth2/operation/listOAuth2Clients
    private static final Pattern NEXT_PAGE_TOKEN_PATTERN = Pattern.compile("^.*page_token=([^>]+)>; rel=\"next\".*$"); // To keep this Link header parsing simple, don't correspond to whole RFC 5988 standard, but only handle current Ory Hydra behaviour described at https://www.ory.sh/docs/ecosystem/api-design#pagination

    public Optional<String> getNextPageToken(HttpHeaders responseHeaders) {
        return responseHeaders.getOrEmpty(HttpHeaders.LINK).stream()
                .findFirst()
                .map(NEXT_PAGE_TOKEN_PATTERN::matcher)
                .filter(Matcher::matches)
                .map(m -> m.group(1));
    }
}
