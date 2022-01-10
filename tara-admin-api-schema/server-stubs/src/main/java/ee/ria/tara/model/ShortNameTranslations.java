package ee.ria.tara.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ShortNameTranslations
 */

public class ShortNameTranslations   {
  @JsonProperty("et")
  private String et;

  @JsonProperty("en")
  private String en;

  @JsonProperty("ru")
  private String ru;

  public ShortNameTranslations et(String et) {
    this.et = et;
    return this;
  }

  /**
   * Get et
   * @return et
  */
  @ApiModelProperty(value = "")

@Pattern(regexp="^(?!\\s*$).+") @Size(min=3,max=40) 
  public String getEt() {
    return et;
  }

  public void setEt(String et) {
    this.et = et;
  }

  public ShortNameTranslations en(String en) {
    this.en = en;
    return this;
  }

  /**
   * Get en
   * @return en
  */
  @ApiModelProperty(value = "")

@Size(min=3,max=40) 
  public String getEn() {
    return en;
  }

  public void setEn(String en) {
    this.en = en;
  }

  public ShortNameTranslations ru(String ru) {
    this.ru = ru;
    return this;
  }

  /**
   * Get ru
   * @return ru
  */
  @ApiModelProperty(value = "")

@Size(min=3,max=40) 
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
    ShortNameTranslations shortNameTranslations = (ShortNameTranslations) o;
    return Objects.equals(this.et, shortNameTranslations.et) &&
        Objects.equals(this.en, shortNameTranslations.en) &&
        Objects.equals(this.ru, shortNameTranslations.ru);
  }

  @Override
  public int hashCode() {
    return Objects.hash(et, en, ru);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShortNameTranslations {\n");
    
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

