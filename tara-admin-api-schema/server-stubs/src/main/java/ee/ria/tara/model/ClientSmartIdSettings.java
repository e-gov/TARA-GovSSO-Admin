package ee.ria.tara.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * ClientSmartIdSettings
 */

public class ClientSmartIdSettings   {
  @JsonProperty("relying_party_UUID")
  private String relyingPartyUUID;

  @JsonProperty("relying_party_name")
  private String relyingPartyName;

  @JsonProperty("should_use_additional_verification_code_check")
  private Boolean shouldUseAdditionalVerificationCodeCheck;

  public ClientSmartIdSettings relyingPartyUUID(String relyingPartyUUID) {
    this.relyingPartyUUID = relyingPartyUUID;
    return this;
  }

  /**
   * Get relyingPartyUUID
   * @return relyingPartyUUID
  */
  @ApiModelProperty(value = "")

@Size(max=512) 
  public String getRelyingPartyUUID() {
    return relyingPartyUUID;
  }

  public void setRelyingPartyUUID(String relyingPartyUUID) {
    this.relyingPartyUUID = relyingPartyUUID;
  }

  public ClientSmartIdSettings relyingPartyName(String relyingPartyName) {
    this.relyingPartyName = relyingPartyName;
    return this;
  }

  /**
   * Get relyingPartyName
   * @return relyingPartyName
  */
  @ApiModelProperty(value = "")

@Size(max=512) 
  public String getRelyingPartyName() {
    return relyingPartyName;
  }

  public void setRelyingPartyName(String relyingPartyName) {
    this.relyingPartyName = relyingPartyName;
  }

  public ClientSmartIdSettings shouldUseAdditionalVerificationCodeCheck(Boolean shouldUseAdditionalVerificationCodeCheck) {
    this.shouldUseAdditionalVerificationCodeCheck = shouldUseAdditionalVerificationCodeCheck;
    return this;
  }

  /**
   * Get shouldUseAdditionalVerificationCodeCheck
   * @return shouldUseAdditionalVerificationCodeCheck
  */
  @ApiModelProperty(value = "")


  public Boolean getShouldUseAdditionalVerificationCodeCheck() {
    return shouldUseAdditionalVerificationCodeCheck;
  }

  public void setShouldUseAdditionalVerificationCodeCheck(Boolean shouldUseAdditionalVerificationCodeCheck) {
    this.shouldUseAdditionalVerificationCodeCheck = shouldUseAdditionalVerificationCodeCheck;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClientSmartIdSettings clientSmartIdSettings = (ClientSmartIdSettings) o;
    return Objects.equals(this.relyingPartyUUID, clientSmartIdSettings.relyingPartyUUID) &&
        Objects.equals(this.relyingPartyName, clientSmartIdSettings.relyingPartyName) &&
        Objects.equals(this.shouldUseAdditionalVerificationCodeCheck, clientSmartIdSettings.shouldUseAdditionalVerificationCodeCheck);
  }

  @Override
  public int hashCode() {
    return Objects.hash(relyingPartyUUID, relyingPartyName, shouldUseAdditionalVerificationCodeCheck);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClientSmartIdSettings {\n");
    
    sb.append("    relyingPartyUUID: ").append(toIndentedString(relyingPartyUUID)).append("\n");
    sb.append("    relyingPartyName: ").append(toIndentedString(relyingPartyName)).append("\n");
    sb.append("    shouldUseAdditionalVerificationCodeCheck: ").append(toIndentedString(shouldUseAdditionalVerificationCodeCheck)).append("\n");
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

