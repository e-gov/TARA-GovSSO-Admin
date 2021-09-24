package ee.ria.tara.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;

/**
 * InstitutionMetainfo
 */

public class InstitutionMetainfo   {
  @JsonProperty("name")
  private String name;

  @JsonProperty("registry_code")
  private String registryCode;

  @JsonProperty("type")
  private InstitutionType type = null;

  public InstitutionMetainfo name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @ApiModelProperty(example = "Example Institution", required = true, value = "")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public InstitutionMetainfo registryCode(String registryCode) {
    this.registryCode = registryCode;
    return this;
  }

  /**
   * Get registryCode
   * @return registryCode
  */
  @ApiModelProperty(example = "12345678", required = true, value = "")
  @NotNull

@Pattern(regexp="\\d{3,}") 
  public String getRegistryCode() {
    return registryCode;
  }

  public void setRegistryCode(String registryCode) {
    this.registryCode = registryCode;
  }

  public InstitutionMetainfo type(InstitutionType type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public InstitutionType getType() {
    return type;
  }

  public void setType(InstitutionType type) {
    this.type = type;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InstitutionMetainfo institutionMetainfo = (InstitutionMetainfo) o;
    return Objects.equals(this.name, institutionMetainfo.name) &&
        Objects.equals(this.registryCode, institutionMetainfo.registryCode) &&
        Objects.equals(this.type, institutionMetainfo.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, registryCode, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InstitutionMetainfo {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    registryCode: ").append(toIndentedString(registryCode)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

