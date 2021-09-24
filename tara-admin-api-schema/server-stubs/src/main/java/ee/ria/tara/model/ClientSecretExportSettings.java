package ee.ria.tara.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ClientSecretExportSettings
 */

public class ClientSecretExportSettings   {
  @JsonProperty("recipient_id_code")
  private String recipientIdCode;

  @JsonProperty("recipient_name_in_ldap")
  private String recipientNameInLdap;

  @JsonProperty("recipient_email")
  private String recipientEmail;

  public ClientSecretExportSettings recipientIdCode(String recipientIdCode) {
    this.recipientIdCode = recipientIdCode;
    return this;
  }

  /**
   * Get recipientIdCode
   * @return recipientIdCode
  */
  @ApiModelProperty(example = "60001019906", value = "")

@Pattern(regexp="^[0-9]{11,11}$") 
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
  @ApiModelProperty(example = "Mari-Liis Männik", value = "")

@Size(min=5) 
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
  @ApiModelProperty(example = "60001019906@eesti.ee", value = "")

@Pattern(regexp="(^.*@.*\\..*$)") @Size(min=5) 
  public String getRecipientEmail() {
    return recipientEmail;
  }

  public void setRecipientEmail(String recipientEmail) {
    this.recipientEmail = recipientEmail;
  }


  @Override
  public boolean equals(java.lang.Object o) {
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
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

