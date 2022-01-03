package ee.ria.tara.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * NameTranslations
 */

public class NameTranslations   {
  @JsonProperty("et")
  private String et;

  @JsonProperty("en")
  private String en;

  @JsonProperty("ru")
  private String ru;

  public NameTranslations et(String et) {
    this.et = et;
    return this;
  }

  /**
   * Get et
   * @return et
  */
  @ApiModelProperty(value = "")

@Pattern(regexp="^(?!\\s*$).+") @Size(min=3,max=150) 
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
  @ApiModelProperty(value = "")

@Size(min=3,max=150) 
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
  @ApiModelProperty(value = "")

@Size(min=3,max=150) 
  public String getRu() {
    return ru;
  }

  public void setRu(String ru) {
    this.ru = ru;
  }


  @Override
  public boolean equals(java.lang.Object o) {
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
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

