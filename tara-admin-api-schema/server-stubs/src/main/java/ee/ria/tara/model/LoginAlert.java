package ee.ria.tara.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import ee.ria.tara.model.MessageTemplate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * LoginAlert
 */

public class LoginAlert   {
  @JsonProperty("enabled")
  private Boolean enabled;

  @JsonProperty("message_templates")
  @Valid
  private List<MessageTemplate> messageTemplates = null;

  @JsonProperty("auth_methods")
  @Valid
  private List<String> authMethods = null;

  public LoginAlert enabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Get enabled
   * @return enabled
  */
  @ApiModelProperty(value = "")


  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public LoginAlert messageTemplates(List<MessageTemplate> messageTemplates) {
    this.messageTemplates = messageTemplates;
    return this;
  }

  public LoginAlert addMessageTemplatesItem(MessageTemplate messageTemplatesItem) {
    if (this.messageTemplates == null) {
      this.messageTemplates = new ArrayList<>();
    }
    this.messageTemplates.add(messageTemplatesItem);
    return this;
  }

  /**
   * Get messageTemplates
   * @return messageTemplates
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<MessageTemplate> getMessageTemplates() {
    return messageTemplates;
  }

  public void setMessageTemplates(List<MessageTemplate> messageTemplates) {
    this.messageTemplates = messageTemplates;
  }

  public LoginAlert authMethods(List<String> authMethods) {
    this.authMethods = authMethods;
    return this;
  }

  public LoginAlert addAuthMethodsItem(String authMethodsItem) {
    if (this.authMethods == null) {
      this.authMethods = new ArrayList<>();
    }
    this.authMethods.add(authMethodsItem);
    return this;
  }

  /**
   * Get authMethods
   * @return authMethods
  */
  @ApiModelProperty(value = "")


  public List<String> getAuthMethods() {
    return authMethods;
  }

  public void setAuthMethods(List<String> authMethods) {
    this.authMethods = authMethods;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LoginAlert loginAlert = (LoginAlert) o;
    return Objects.equals(this.enabled, loginAlert.enabled) &&
        Objects.equals(this.messageTemplates, loginAlert.messageTemplates) &&
        Objects.equals(this.authMethods, loginAlert.authMethods);
  }

  @Override
  public int hashCode() {
    return Objects.hash(enabled, messageTemplates, authMethods);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoginAlert {\n");
    
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
    sb.append("    messageTemplates: ").append(toIndentedString(messageTemplates)).append("\n");
    sb.append("    authMethods: ").append(toIndentedString(authMethods)).append("\n");
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

