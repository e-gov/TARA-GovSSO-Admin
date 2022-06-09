package ee.ria.tara.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ClientSmartIdSettings
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class ClientSmartIdSettings {

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
  @Size(max = 512) 
  @Schema(name = "relying_party_UUID", required = false)
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
  @Size(max = 512) 
  @Schema(name = "relying_party_name", required = false)
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
  
  @Schema(name = "should_use_additional_verification_code_check", required = false)
  public Boolean getShouldUseAdditionalVerificationCodeCheck() {
    return shouldUseAdditionalVerificationCodeCheck;
  }

  public void setShouldUseAdditionalVerificationCodeCheck(Boolean shouldUseAdditionalVerificationCodeCheck) {
    this.shouldUseAdditionalVerificationCodeCheck = shouldUseAdditionalVerificationCodeCheck;
  }

  @Override
  public boolean equals(Object o) {
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
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

