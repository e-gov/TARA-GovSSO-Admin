package ee.ria.tara.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class HydraClientMetadata   {
  @JsonProperty("display_user_consent")
  @NotNull
  private Boolean displayUserConsent;

  @JsonProperty("oidc_client")
  @NotNull
  private OidcClient oidcClient;
}

