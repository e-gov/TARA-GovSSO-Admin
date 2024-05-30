package ee.ria.tara.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SsoModeResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.6.0")
public class SsoModeResponse {

  private Boolean ssoMode;

  public SsoModeResponse ssoMode(Boolean ssoMode) {
    this.ssoMode = ssoMode;
    return this;
  }

  /**
   * Get ssoMode
   * @return ssoMode
  */
  
  @Schema(name = "ssoMode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ssoMode")
  public Boolean getSsoMode() {
    return ssoMode;
  }

  public void setSsoMode(Boolean ssoMode) {
    this.ssoMode = ssoMode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SsoModeResponse ssoModeResponse = (SsoModeResponse) o;
    return Objects.equals(this.ssoMode, ssoModeResponse.ssoMode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ssoMode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SsoModeResponse {\n");
    sb.append("    ssoMode: ").append(toIndentedString(ssoMode)).append("\n");
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

