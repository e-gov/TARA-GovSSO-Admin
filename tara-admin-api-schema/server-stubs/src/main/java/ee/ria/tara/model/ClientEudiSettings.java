package ee.ria.tara.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ClientEudiSettings
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.15.0")
public class ClientEudiSettings {

  private @Nullable String presentationConfigId;

  public ClientEudiSettings presentationConfigId(@Nullable String presentationConfigId) {
    this.presentationConfigId = presentationConfigId;
    return this;
  }

  /**
   * Get presentationConfigId
   * @return presentationConfigId
   */
  @Size(max = 512) 
  @Schema(name = "presentation_config_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("presentation_config_id")
  public @Nullable String getPresentationConfigId() {
    return presentationConfigId;
  }

  public void setPresentationConfigId(@Nullable String presentationConfigId) {
    this.presentationConfigId = presentationConfigId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClientEudiSettings clientEudiSettings = (ClientEudiSettings) o;
    return Objects.equals(this.presentationConfigId, clientEudiSettings.presentationConfigId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(presentationConfigId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClientEudiSettings {\n");
    sb.append("    presentationConfigId: ").append(toIndentedString(presentationConfigId)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

