package ee.ria.tara.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import ee.ria.tara.model.InstitutionType;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * InstitutionMetainfo
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class InstitutionMetainfo {

  private String name;

  private String registryCode;

  private InstitutionType type;

  public InstitutionMetainfo() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public InstitutionMetainfo(String name, String registryCode, InstitutionType type) {
    this.name = name;
    this.registryCode = registryCode;
    this.type = type;
  }

  public InstitutionMetainfo name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "Example Institution", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
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
  @NotNull @Pattern(regexp = "\\d{3,}") 
  @Schema(name = "registry_code", example = "12345678", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("registry_code")
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
  @NotNull @Valid 
  @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public InstitutionType getType() {
    return type;
  }

  public void setType(InstitutionType type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
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
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

