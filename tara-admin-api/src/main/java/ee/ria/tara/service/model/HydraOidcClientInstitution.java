package ee.ria.tara.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class HydraOidcClientInstitution   {
  @JsonProperty("registry_code")
  @NotNull
  private String registryCode;

  @JsonProperty("sector")
  @NotNull
  private String sector;
}
