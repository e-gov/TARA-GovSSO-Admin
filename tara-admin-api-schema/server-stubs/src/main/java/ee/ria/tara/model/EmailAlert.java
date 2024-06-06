package ee.ria.tara.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import ee.ria.tara.model.MessageTemplate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * EmailAlert
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.5.0")
public class EmailAlert {

  private Boolean enabled;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime sendAt;

  @Valid
  private List<@Valid MessageTemplate> messageTemplates;

  public EmailAlert enabled(Boolean enabled) {
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

  public EmailAlert sendAt(OffsetDateTime sendAt) {
    this.sendAt = sendAt;
    return this;
  }

  /**
   * Get sendAt
   * @return sendAt
  */
  @Valid 
  @Schema(name = "send_at", example = "2019-08-24T14:15:22Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("send_at")
  public OffsetDateTime getSendAt() {
    return sendAt;
  }

  public void setSendAt(OffsetDateTime sendAt) {
    this.sendAt = sendAt;
  }

  public EmailAlert messageTemplates(List<@Valid MessageTemplate> messageTemplates) {
    this.messageTemplates = messageTemplates;
    return this;
  }

  public EmailAlert addMessageTemplatesItem(MessageTemplate messageTemplatesItem) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmailAlert emailAlert = (EmailAlert) o;
    return Objects.equals(this.enabled, emailAlert.enabled) &&
        Objects.equals(this.sendAt, emailAlert.sendAt) &&
        Objects.equals(this.messageTemplates, emailAlert.messageTemplates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(enabled, sendAt, messageTemplates);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmailAlert {\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
    sb.append("    sendAt: ").append(toIndentedString(sendAt)).append("\n");
    sb.append("    messageTemplates: ").append(toIndentedString(messageTemplates)).append("\n");
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

