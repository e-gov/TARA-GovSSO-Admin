package ee.ria.tara.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import ee.ria.tara.model.InstitutionBillingSettings;
import ee.ria.tara.model.InstitutionType;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Institution
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.8.0")
public class Institution {

  private String id;

  private String registryCode;

  private String name;

  private InstitutionType type;

  @Valid
  private List<String> clientIds;

  private String address;

  private String phone;

  private String email;

  private InstitutionBillingSettings billingSettings;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public Institution() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Institution(String registryCode, String name, InstitutionType type, String address, String phone, String email, InstitutionBillingSettings billingSettings) {
    this.registryCode = registryCode;
    this.name = name;
    this.type = type;
    this.address = address;
    this.phone = phone;
    this.email = email;
    this.billingSettings = billingSettings;
  }

  public Institution id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Institution registryCode(String registryCode) {
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

  public Institution name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @NotNull @Size(min = 3, max = 150) 
  @Schema(name = "name", example = "Example Institution", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Institution type(InstitutionType type) {
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

  public Institution clientIds(List<String> clientIds) {
    this.clientIds = clientIds;
    return this;
  }

  public Institution addClientIdsItem(String clientIdsItem) {
    if (this.clientIds == null) {
      this.clientIds = new ArrayList<>();
    }
    this.clientIds.add(clientIdsItem);
    return this;
  }

  /**
   * Get clientIds
   * @return clientIds
   */
  
  @Schema(name = "client_ids", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_ids")
  public List<String> getClientIds() {
    return clientIds;
  }

  public void setClientIds(List<String> clientIds) {
    this.clientIds = clientIds;
  }

  public Institution address(String address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  @NotNull @Size(min = 3, max = 512) 
  @Schema(name = "address", example = "Test st 123", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Institution phone(String phone) {
    this.phone = phone;
    return this;
  }

  /**
   * Get phone
   * @return phone
   */
  @NotNull @Pattern(regexp = "^[0-9\\+]{5,}$") 
  @Schema(name = "phone", example = "+3726630200", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("phone")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Institution email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  @NotNull @Pattern(regexp = "(^.*@.*\\..*$)") @Size(min = 5) @jakarta.validation.constraints.Email 
  @Schema(name = "email", example = "info@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Institution billingSettings(InstitutionBillingSettings billingSettings) {
    this.billingSettings = billingSettings;
    return this;
  }

  /**
   * Get billingSettings
   * @return billingSettings
   */
  @NotNull @Valid 
  @Schema(name = "billing_settings", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("billing_settings")
  public InstitutionBillingSettings getBillingSettings() {
    return billingSettings;
  }

  public void setBillingSettings(InstitutionBillingSettings billingSettings) {
    this.billingSettings = billingSettings;
  }

  public Institution createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @Valid 
  @Schema(name = "created_at", example = "2019-08-24T14:15:22Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("created_at")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Institution updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
   */
  @Valid 
  @Schema(name = "updated_at", example = "2019-08-24T14:15:22Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updated_at")
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Institution institution = (Institution) o;
    return Objects.equals(this.id, institution.id) &&
        Objects.equals(this.registryCode, institution.registryCode) &&
        Objects.equals(this.name, institution.name) &&
        Objects.equals(this.type, institution.type) &&
        Objects.equals(this.clientIds, institution.clientIds) &&
        Objects.equals(this.address, institution.address) &&
        Objects.equals(this.phone, institution.phone) &&
        Objects.equals(this.email, institution.email) &&
        Objects.equals(this.billingSettings, institution.billingSettings) &&
        Objects.equals(this.createdAt, institution.createdAt) &&
        Objects.equals(this.updatedAt, institution.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, registryCode, name, type, clientIds, address, phone, email, billingSettings, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Institution {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    registryCode: ").append(toIndentedString(registryCode)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    clientIds: ").append(toIndentedString(clientIds)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    billingSettings: ").append(toIndentedString(billingSettings)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

