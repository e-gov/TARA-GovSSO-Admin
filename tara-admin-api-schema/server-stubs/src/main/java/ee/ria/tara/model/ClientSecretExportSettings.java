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
 * ClientSecretExportSettings
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.13.0")
public class ClientSecretExportSettings {

  private String recipientIdCode;

  private String recipientEmail;

  public ClientSecretExportSettings() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ClientSecretExportSettings(String recipientIdCode, String recipientEmail) {
    this.recipientIdCode = recipientIdCode;
    this.recipientEmail = recipientEmail;
  }

  public ClientSecretExportSettings recipientIdCode(String recipientIdCode) {
    this.recipientIdCode = recipientIdCode;
    return this;
  }

  /**
   * Get recipientIdCode
   * @return recipientIdCode
   */
  @NotNull @Pattern(regexp = "^[0-9]{11}$") 
  @Schema(name = "recipient_id_code", example = "60001019906", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("recipient_id_code")
  public String getRecipientIdCode() {
    return recipientIdCode;
  }

  public void setRecipientIdCode(String recipientIdCode) {
    this.recipientIdCode = recipientIdCode;
  }

  public ClientSecretExportSettings recipientEmail(String recipientEmail) {
    this.recipientEmail = recipientEmail;
    return this;
  }

  /**
   * Get recipientEmail
   * @return recipientEmail
   */
  @NotNull @Pattern(regexp = "(^.*@.*\\..*$)") @Size(min = 5) @jakarta.validation.constraints.Email 
  @Schema(name = "recipient_email", example = "60001019906@eesti.ee", requiredMode = Schema.RequiredMode.REQUIRED)
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
        Objects.equals(this.recipientEmail, clientSecretExportSettings.recipientEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(recipientIdCode, recipientEmail);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClientSecretExportSettings {\n");
    sb.append("    recipientIdCode: ").append(toIndentedString(recipientIdCode)).append("\n");
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

