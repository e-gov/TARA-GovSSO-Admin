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
 * NameTranslations
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.11.0")
public class NameTranslations {

  private @Nullable String et;

  private @Nullable String en;

  private @Nullable String ru;

  public NameTranslations et(String et) {
    this.et = et;
    return this;
  }

  /**
   * Get et
   * @return et
   */
  @Pattern(regexp = "^(?!\\s*$).+") @Size(min = 3, max = 150) 
  @Schema(name = "et", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("et")
  public String getEt() {
    return et;
  }

  public void setEt(String et) {
    this.et = et;
  }

  public NameTranslations en(String en) {
    this.en = en;
    return this;
  }

  /**
   * Get en
   * @return en
   */
  @Size(min = 3, max = 150) 
  @Schema(name = "en", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("en")
  public String getEn() {
    return en;
  }

  public void setEn(String en) {
    this.en = en;
  }

  public NameTranslations ru(String ru) {
    this.ru = ru;
    return this;
  }

  /**
   * Get ru
   * @return ru
   */
  @Size(min = 3, max = 150) 
  @Schema(name = "ru", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ru")
  public String getRu() {
    return ru;
  }

  public void setRu(String ru) {
    this.ru = ru;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NameTranslations nameTranslations = (NameTranslations) o;
    return Objects.equals(this.et, nameTranslations.et) &&
        Objects.equals(this.en, nameTranslations.en) &&
        Objects.equals(this.ru, nameTranslations.ru);
  }

  @Override
  public int hashCode() {
    return Objects.hash(et, en, ru);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NameTranslations {\n");
    sb.append("    et: ").append(toIndentedString(et)).append("\n");
    sb.append("    en: ").append(toIndentedString(en)).append("\n");
    sb.append("    ru: ").append(toIndentedString(ru)).append("\n");
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

