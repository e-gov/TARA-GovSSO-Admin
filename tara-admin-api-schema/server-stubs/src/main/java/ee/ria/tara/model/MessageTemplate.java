package ee.ria.tara.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * MessageTemplate
 */

public class MessageTemplate   {
  @JsonProperty("message")
  private String message;

  @JsonProperty("locale")
  private String locale;

  public MessageTemplate message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
  */
  @ApiModelProperty(example = "Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00", value = "")

@Size(min=1,max=255) 
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public MessageTemplate locale(String locale) {
    this.locale = locale;
    return this;
  }

  /**
   * Get locale
   * @return locale
  */
  @ApiModelProperty(example = "et", value = "")


  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MessageTemplate messageTemplate = (MessageTemplate) o;
    return Objects.equals(this.message, messageTemplate.message) &&
        Objects.equals(this.locale, messageTemplate.locale);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, locale);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MessageTemplate {\n");
    
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    locale: ").append(toIndentedString(locale)).append("\n");
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

