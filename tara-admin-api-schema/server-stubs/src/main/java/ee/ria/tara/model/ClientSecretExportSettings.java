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
 * ClientSecretExportSettings
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.8.0")
public class ClientSecretExportSettings {

  private String recipientIdCode;

  private String recipientNameInLdap;

  private String recipientEmail;

  public ClientSecretExportSettings recipientIdCode(String recipientIdCode) {
    this.recipientIdCode = recipientIdCode;
    return this;
  }

  /**
   * Get recipientIdCode
   * @return recipientIdCode
   */
  @Pattern(regexp = "^[0-9]{11,11}$") 
  @Schema(name = "recipient_id_code", example = "60001019906", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("recipient_id_code")
  public String getRecipientIdCode() {
    return recipientIdCode;
  }

  public void setRecipientIdCode(String recipientIdCode) {
    this.recipientIdCode = recipientIdCode;
  }

  public ClientSecretExportSettings recipientNameInLdap(String recipientNameInLdap) {
    this.recipientNameInLdap = recipientNameInLdap;
    return this;
  }

  /**
   * Get recipientNameInLdap
   * @return recipientNameInLdap
   */
  @Size(min = 5) 
  @Schema(name = "recipient_name_in_ldap", example = "Mari-Liis Männik", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("recipient_name_in_ldap")
  public String getRecipientNameInLdap() {
    return recipientNameInLdap;
  }

  public void setRecipientNameInLdap(String recipientNameInLdap) {
    this.recipientNameInLdap = recipientNameInLdap;
  }

  public ClientSecretExportSettings recipientEmail(String recipientEmail) {
    this.recipientEmail = recipientEmail;
    return this;
  }

  /**
   * Get recipientEmail
   * @return recipientEmail
   */
  @Pattern(regexp = "(^.*@.*\\..*$)") @Size(min = 5) @jakarta.validation.constraints.Email 
  @Schema(name = "recipient_email", example = "60001019906@eesti.ee", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("recipient_email")
  public String getRecipientEmail() {
    return recipientEmail;
  }

  public void setRecipientEmail(String recipientEmail) {
    this.recipientEmail = recipientEmail;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClientSecretExportSettings clientSecretExportSettings = (ClientSecretExportSettings) o;
    return Objects.equals(this.recipientIdCode, clientSecretExportSettings.recipientIdCode) &&
        Objects.equals(this.recipientNameInLdap, clientSecretExportSettings.recipientNameInLdap) &&
        Objects.equals(this.recipientEmail, clientSecretExportSettings.recipientEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(recipientIdCode, recipientNameInLdap, recipientEmail);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClientSecretExportSettings {\n");
    sb.append("    recipientIdCode: ").append(toIndentedString(recipientIdCode)).append("\n");
    sb.append("    recipientNameInLdap: ").append(toIndentedString(recipientNameInLdap)).append("\n");
    sb.append("    recipientEmail: ").append(toIndentedString(recipientEmail)).append("\n");
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

