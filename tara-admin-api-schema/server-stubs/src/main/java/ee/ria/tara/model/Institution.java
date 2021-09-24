package ee.ria.tara.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import ee.ria.tara.model.InstitutionBillingSettings;
import ee.ria.tara.model.InstitutionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Institution
 */

public class Institution   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("registry_code")
  private String registryCode;

  @JsonProperty("name")
  private String name;

  @JsonProperty("type")
  private InstitutionType type = null;

  @JsonProperty("client_ids")
  @Valid
  private List<String> clientIds = null;

  @JsonProperty("address")
  private String address;

  @JsonProperty("phone")
  private String phone;

  @JsonProperty("email")
  private String email;

  @JsonProperty("billing_settings")
  private InstitutionBillingSettings billingSettings = null;

  @JsonProperty("created_at")
  private OffsetDateTime createdAt;

  @JsonProperty("updated_at")
  private OffsetDateTime updatedAt;

  public Institution id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @ApiModelProperty(value = "")


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
  @ApiModelProperty(example = "12345678", required = true, value = "")
  @NotNull

@Pattern(regexp="\\d{3,}") 
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
  @ApiModelProperty(example = "Example Institution", required = true, value = "")
  @NotNull

@Size(min=3,max=150) 
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
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

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
  @ApiModelProperty(value = "")


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
  @ApiModelProperty(example = "Test st 123", required = true, value = "")
  @NotNull

@Size(min=3,max=512) 
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
  @ApiModelProperty(example = "+3726630200", required = true, value = "")
  @NotNull

@Pattern(regexp="^[0-9\\+]{5,}$") 
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
  @ApiModelProperty(example = "info@example.com", required = true, value = "")
  @NotNull

@Pattern(regexp="(^.*@.*\\..*$)") @Size(min=5) 
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
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

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
  @ApiModelProperty(value = "")

  @Valid

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
  @ApiModelProperty(value = "")

  @Valid

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }


  @Override
  public boolean equals(java.lang.Object o) {
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
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

