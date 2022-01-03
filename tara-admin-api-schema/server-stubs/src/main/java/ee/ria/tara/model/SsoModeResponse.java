package ee.ria.tara.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * SsoModeResponse
 */

public class SsoModeResponse   {
  @JsonProperty("ssoMode")
  private Boolean ssoMode;

  public SsoModeResponse ssoMode(Boolean ssoMode) {
    this.ssoMode = ssoMode;
    return this;
  }

  /**
   * Get ssoMode
   * @return ssoMode
  */
  @ApiModelProperty(value = "")


  public Boolean getSsoMode() {
    return ssoMode;
  }

  public void setSsoMode(Boolean ssoMode) {
    this.ssoMode = ssoMode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
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
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

