package ee.ria.tara.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class HydraClient {
    @JsonProperty("client_id")
    @Pattern(regexp="^(?!\\s*$).+") @Size(max=255)
    private String clientId;

    @JsonProperty("client_name")
    @Pattern(regexp="^(?!\\s*$).+") @Size(max=512)
    private String clientName;

    @JsonProperty("client_secret")
    @Pattern(regexp="-A-Za-z0-9")
    private String clientSecret;

    @ToString.Include(name = "clientSecret")
    public String clientSecretToString() {
        if (clientSecret == null) {
            return "null";
        }
        return "[non-null value]";
    }

    @JsonProperty("access_token_strategy")
    private String accessTokenStrategy;

    @JsonProperty("audience")
    private List<String> audience;

    @JsonProperty("authorization_code_grant_access_token_lifespan")
    @Pattern(regexp="^([0-9]+(ns|us|ms|s|m|h))*$")
    private String authorizationCodeGrantAccessTokenLifespan;

    @JsonProperty("refresh_token_grant_access_token_lifespan")
    @Pattern(regexp="^([0-9]+(ns|us|ms|s|m|h))*$")
    private String refreshTokenGrantAccessTokenLifespan;

    @JsonProperty("grant_types")
    @Size(min=1)
    private List<String> grantTypes = List.of("authorization_code");

    @JsonProperty("redirect_uris")
    @Size(min=1)
    private List<String> redirectUris;

    @JsonProperty("post_logout_redirect_uris")
    @Size(min=1)
    private List<String> postLogoutRedirectUris;

    @JsonProperty("response_types")
    private List<String> responseTypes = List.of("code");

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("subject_type")
    private String subjectType = "public";

    @JsonProperty("token_endpoint_auth_method")
    private String tokenEndpointAuthMethod;

    @JsonProperty("backchannel_logout_uri")
    private String backchannelLogoutUri;

    @JsonProperty("backchannel_logout_session_required")
    private Boolean backchannelLogoutSessionRequired = true;

    @JsonProperty("metadata")
    private HydraClientMetadata metadata;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;
}
