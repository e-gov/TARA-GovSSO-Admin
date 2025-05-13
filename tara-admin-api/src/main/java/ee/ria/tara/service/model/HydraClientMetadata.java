package ee.ria.tara.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.ria.tara.model.Client;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class HydraClientMetadata   {
  @JsonProperty("display_user_consent")
  @NotNull
  private Boolean displayUserConsent;

  @JsonProperty("skip_user_consent_client_ids")
  @NotNull
  private List<String> skipUserConsentClientIds;

  @JsonProperty("oidc_client")
  @NotNull
  private OidcClient oidcClient;

  @JsonProperty("paasuke_parameters")
  @NotNull
  private String paasukeParameters;

  @JsonProperty("minimum_acr_value")
  private Client.MinimumAcrValueEnum minimumAcrValue;
}

