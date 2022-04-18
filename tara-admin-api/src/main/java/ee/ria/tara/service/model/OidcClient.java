package ee.ria.tara.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.ria.tara.model.ClientMidSettings;
import ee.ria.tara.model.ClientSmartIdSettings;
import ee.ria.tara.model.NameTranslations;
import ee.ria.tara.model.ShortNameTranslations;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class OidcClient {
  @JsonProperty("name")
  private String name;

  @JsonProperty("name_translations")
  @NotNull
  private NameTranslations nameTranslations;

  @JsonProperty("short_name")
  private String shortName;

  @JsonProperty("short_name_translations")
  @NotNull
  private ShortNameTranslations shortNameTranslations;

  @JsonProperty("legacy_return_url")
  private String legacyReturnUrl;

  @JsonProperty("eidas_requester_id")
  private String eidasRequesterId;

  @JsonProperty("institution")
  @NotNull
  private HydraOidcClientInstitution institution;

  @JsonProperty("mid_settings")
  @NotNull
  private ClientMidSettings midSettings;

  @JsonProperty("smartid_settings")
  @NotNull
  private ClientSmartIdSettings smartidSettings;

  @JsonProperty("logo")
  private byte[] logo;
}

