package ee.ria.tara.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import ee.ria.tara.model.MessageTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * LoginAlert
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.13.0")
public class LoginAlert {

  private @Nullable Boolean enabled;

  @Valid
  private @Nullable List<@Valid MessageTemplate> messageTemplates;

  @Valid
  private @Nullable List<String> authMethods;

  public LoginAlert enabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Get enabled
   * @return enabled
   */
  
  @Schema(name = "enabled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("enabled")
  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public LoginAlert messageTemplates(List<@Valid MessageTemplate> messageTemplates) {
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
  @Valid 
  @Schema(name = "message_templates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message_templates")
  public List<@Valid MessageTemplate> getMessageTemplates() {
    return messageTemplates;
  }

  public void setMessageTemplates(List<@Valid MessageTemplate> messageTemplates) {
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
  
  @Schema(name = "auth_methods", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("auth_methods")
  public List<String> getAuthMethods() {
    return authMethods;
  }

  public void setAuthMethods(List<String> authMethods) {
    this.authMethods = authMethods;
  }

  @Override
  public boolean equals(Object o) {
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
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

