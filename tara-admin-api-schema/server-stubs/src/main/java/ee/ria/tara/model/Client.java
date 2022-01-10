package ee.ria.tara.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import ee.ria.tara.model.ClientContact;
import ee.ria.tara.model.ClientMidSettings;
import ee.ria.tara.model.ClientSecretExportSettings;
import ee.ria.tara.model.ClientSmartIdSettings;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.model.NameTranslations;
import ee.ria.tara.model.ShortNameTranslations;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Client
 */

public class Client   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("client_id")
  private String clientId;

  @JsonProperty("client_name")
  private NameTranslations clientName = null;

  @JsonProperty("client_short_name")
  private ShortNameTranslations clientShortName = null;

  @JsonProperty("client_secret_export_settings")
  private ClientSecretExportSettings clientSecretExportSettings = null;

  @JsonProperty("institution_metainfo")
  private InstitutionMetainfo institutionMetainfo = null;

  @JsonProperty("redirect_uris")
  @Valid
  private List<String> redirectUris = new ArrayList<>();

  @JsonProperty("post_logout_redirect_uris")
  @Valid
  private List<String> postLogoutRedirectUris = new ArrayList<>();

  @JsonProperty("scope")
  @Valid
  private List<String> scope = new ArrayList<>();

  @JsonProperty("secret")
  private String secret;

  @JsonProperty("description")
  private String description;

  @JsonProperty("info_notification_emails")
  @Valid
  private List<String> infoNotificationEmails = null;

  @JsonProperty("sla_notification_emails")
  @Valid
  private List<String> slaNotificationEmails = null;

  @JsonProperty("is_user_consent_required")
  private Boolean isUserConsentRequired;

  @JsonProperty("client_url")
  private String clientUrl;

  @JsonProperty("backchannel_logout_uri")
  private String backchannelLogoutUri;

  @JsonProperty("mid_settings")
  private ClientMidSettings midSettings = null;

  @JsonProperty("smartid_settings")
  private ClientSmartIdSettings smartidSettings = null;

  @JsonProperty("client_contacts")
  @Valid
  private List<ClientContact> clientContacts = null;

  @JsonProperty("created_at")
  private OffsetDateTime createdAt;

  @JsonProperty("updated_at")
  private OffsetDateTime updatedAt;

  @JsonProperty("client_logo")
  private byte[] clientLogo;

  public Client id(String id) {
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

  public Client clientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

  /**
   * Get clientId
   * @return clientId
  */
  @ApiModelProperty(example = "openIdDemo", required = true, value = "")
  @NotNull

@Pattern(regexp="^(?!\\s*$).+") @Size(min=3,max=255) 
  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public Client clientName(NameTranslations clientName) {
    this.clientName = clientName;
    return this;
  }

  /**
   * Get clientName
   * @return clientName
  */
  @ApiModelProperty(value = "")

  @Valid

  public NameTranslations getClientName() {
    return clientName;
  }

  public void setClientName(NameTranslations clientName) {
    this.clientName = clientName;
  }

  public Client clientShortName(ShortNameTranslations clientShortName) {
    this.clientShortName = clientShortName;
    return this;
  }

  /**
   * Get clientShortName
   * @return clientShortName
  */
  @ApiModelProperty(value = "")

  @Valid

  public ShortNameTranslations getClientShortName() {
    return clientShortName;
  }

  public void setClientShortName(ShortNameTranslations clientShortName) {
    this.clientShortName = clientShortName;
  }

  public Client clientSecretExportSettings(ClientSecretExportSettings clientSecretExportSettings) {
    this.clientSecretExportSettings = clientSecretExportSettings;
    return this;
  }

  /**
   * Get clientSecretExportSettings
   * @return clientSecretExportSettings
  */
  @ApiModelProperty(value = "")

  @Valid

  public ClientSecretExportSettings getClientSecretExportSettings() {
    return clientSecretExportSettings;
  }

  public void setClientSecretExportSettings(ClientSecretExportSettings clientSecretExportSettings) {
    this.clientSecretExportSettings = clientSecretExportSettings;
  }

  public Client institutionMetainfo(InstitutionMetainfo institutionMetainfo) {
    this.institutionMetainfo = institutionMetainfo;
    return this;
  }

  /**
   * Get institutionMetainfo
   * @return institutionMetainfo
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public InstitutionMetainfo getInstitutionMetainfo() {
    return institutionMetainfo;
  }

  public void setInstitutionMetainfo(InstitutionMetainfo institutionMetainfo) {
    this.institutionMetainfo = institutionMetainfo;
  }

  public Client redirectUris(List<String> redirectUris) {
    this.redirectUris = redirectUris;
    return this;
  }

  public Client addRedirectUrisItem(String redirectUrisItem) {
    this.redirectUris.add(redirectUrisItem);
    return this;
  }

  /**
   * Get redirectUris
   * @return redirectUris
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

@Size(min=1) 
  public List<String> getRedirectUris() {
    return redirectUris;
  }

  public void setRedirectUris(List<String> redirectUris) {
    this.redirectUris = redirectUris;
  }

  public Client postLogoutRedirectUris(List<String> postLogoutRedirectUris) {
    this.postLogoutRedirectUris = postLogoutRedirectUris;
    return this;
  }

  public Client addPostLogoutRedirectUrisItem(String postLogoutRedirectUrisItem) {
    this.postLogoutRedirectUris.add(postLogoutRedirectUrisItem);
    return this;
  }

  /**
   * Get postLogoutRedirectUris
   * @return postLogoutRedirectUris
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

@Size(min=1) 
  public List<String> getPostLogoutRedirectUris() {
    return postLogoutRedirectUris;
  }

  public void setPostLogoutRedirectUris(List<String> postLogoutRedirectUris) {
    this.postLogoutRedirectUris = postLogoutRedirectUris;
  }

  public Client scope(List<String> scope) {
    this.scope = scope;
    return this;
  }

  public Client addScopeItem(String scopeItem) {
    this.scope.add(scopeItem);
    return this;
  }

  /**
   * Get scope
   * @return scope
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

@Size(min=1) 
  public List<String> getScope() {
    return scope;
  }

  public void setScope(List<String> scope) {
    this.scope = scope;
  }

  public Client secret(String secret) {
    this.secret = secret;
    return this;
  }

  /**
   * Get secret
   * @return secret
  */
  @ApiModelProperty(value = "")


  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public Client description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
  */
  @ApiModelProperty(value = "")

@Size(min=3,max=4000) 
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Client infoNotificationEmails(List<String> infoNotificationEmails) {
    this.infoNotificationEmails = infoNotificationEmails;
    return this;
  }

  public Client addInfoNotificationEmailsItem(String infoNotificationEmailsItem) {
    if (this.infoNotificationEmails == null) {
      this.infoNotificationEmails = new ArrayList<>();
    }
    this.infoNotificationEmails.add(infoNotificationEmailsItem);
    return this;
  }

  /**
   * Get infoNotificationEmails
   * @return infoNotificationEmails
  */
  @ApiModelProperty(value = "")


  public List<String> getInfoNotificationEmails() {
    return infoNotificationEmails;
  }

  public void setInfoNotificationEmails(List<String> infoNotificationEmails) {
    this.infoNotificationEmails = infoNotificationEmails;
  }

  public Client slaNotificationEmails(List<String> slaNotificationEmails) {
    this.slaNotificationEmails = slaNotificationEmails;
    return this;
  }

  public Client addSlaNotificationEmailsItem(String slaNotificationEmailsItem) {
    if (this.slaNotificationEmails == null) {
      this.slaNotificationEmails = new ArrayList<>();
    }
    this.slaNotificationEmails.add(slaNotificationEmailsItem);
    return this;
  }

  /**
   * Get slaNotificationEmails
   * @return slaNotificationEmails
  */
  @ApiModelProperty(value = "")


  public List<String> getSlaNotificationEmails() {
    return slaNotificationEmails;
  }

  public void setSlaNotificationEmails(List<String> slaNotificationEmails) {
    this.slaNotificationEmails = slaNotificationEmails;
  }

  public Client isUserConsentRequired(Boolean isUserConsentRequired) {
    this.isUserConsentRequired = isUserConsentRequired;
    return this;
  }

  /**
   * Get isUserConsentRequired
   * @return isUserConsentRequired
  */
  @ApiModelProperty(value = "")


  public Boolean getIsUserConsentRequired() {
    return isUserConsentRequired;
  }

  public void setIsUserConsentRequired(Boolean isUserConsentRequired) {
    this.isUserConsentRequired = isUserConsentRequired;
  }

  public Client clientUrl(String clientUrl) {
    this.clientUrl = clientUrl;
    return this;
  }

  /**
   * Get clientUrl
   * @return clientUrl
  */
  @ApiModelProperty(example = "https://client.example.com/", value = "")


  public String getClientUrl() {
    return clientUrl;
  }

  public void setClientUrl(String clientUrl) {
    this.clientUrl = clientUrl;
  }

  public Client backchannelLogoutUri(String backchannelLogoutUri) {
    this.backchannelLogoutUri = backchannelLogoutUri;
    return this;
  }

  /**
   * Get backchannelLogoutUri
   * @return backchannelLogoutUri
  */
  @ApiModelProperty(example = "https://example.com/", value = "")


  public String getBackchannelLogoutUri() {
    return backchannelLogoutUri;
  }

  public void setBackchannelLogoutUri(String backchannelLogoutUri) {
    this.backchannelLogoutUri = backchannelLogoutUri;
  }

  public Client midSettings(ClientMidSettings midSettings) {
    this.midSettings = midSettings;
    return this;
  }

  /**
   * Get midSettings
   * @return midSettings
  */
  @ApiModelProperty(value = "")

  @Valid

  public ClientMidSettings getMidSettings() {
    return midSettings;
  }

  public void setMidSettings(ClientMidSettings midSettings) {
    this.midSettings = midSettings;
  }

  public Client smartidSettings(ClientSmartIdSettings smartidSettings) {
    this.smartidSettings = smartidSettings;
    return this;
  }

  /**
   * Get smartidSettings
   * @return smartidSettings
  */
  @ApiModelProperty(value = "")

  @Valid

  public ClientSmartIdSettings getSmartidSettings() {
    return smartidSettings;
  }

  public void setSmartidSettings(ClientSmartIdSettings smartidSettings) {
    this.smartidSettings = smartidSettings;
  }

  public Client clientContacts(List<ClientContact> clientContacts) {
    this.clientContacts = clientContacts;
    return this;
  }

  public Client addClientContactsItem(ClientContact clientContactsItem) {
    if (this.clientContacts == null) {
      this.clientContacts = new ArrayList<>();
    }
    this.clientContacts.add(clientContactsItem);
    return this;
  }

  /**
   * Get clientContacts
   * @return clientContacts
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<ClientContact> getClientContacts() {
    return clientContacts;
  }

  public void setClientContacts(List<ClientContact> clientContacts) {
    this.clientContacts = clientContacts;
  }

  public Client createdAt(OffsetDateTime createdAt) {
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

  public Client updatedAt(OffsetDateTime updatedAt) {
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

  public Client clientLogo(byte[] clientLogo) {
    this.clientLogo = clientLogo;
    return this;
  }

  /**
   * Get clientLogo
   * @return clientLogo
  */
  @ApiModelProperty(value = "")


  public byte[] getClientLogo() {
    return clientLogo;
  }

  public void setClientLogo(byte[] clientLogo) {
    this.clientLogo = clientLogo;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Client client = (Client) o;
    return Objects.equals(this.id, client.id) &&
        Objects.equals(this.clientId, client.clientId) &&
        Objects.equals(this.clientName, client.clientName) &&
        Objects.equals(this.clientShortName, client.clientShortName) &&
        Objects.equals(this.clientSecretExportSettings, client.clientSecretExportSettings) &&
        Objects.equals(this.institutionMetainfo, client.institutionMetainfo) &&
        Objects.equals(this.redirectUris, client.redirectUris) &&
        Objects.equals(this.postLogoutRedirectUris, client.postLogoutRedirectUris) &&
        Objects.equals(this.scope, client.scope) &&
        Objects.equals(this.secret, client.secret) &&
        Objects.equals(this.description, client.description) &&
        Objects.equals(this.infoNotificationEmails, client.infoNotificationEmails) &&
        Objects.equals(this.slaNotificationEmails, client.slaNotificationEmails) &&
        Objects.equals(this.isUserConsentRequired, client.isUserConsentRequired) &&
        Objects.equals(this.clientUrl, client.clientUrl) &&
        Objects.equals(this.backchannelLogoutUri, client.backchannelLogoutUri) &&
        Objects.equals(this.midSettings, client.midSettings) &&
        Objects.equals(this.smartidSettings, client.smartidSettings) &&
        Objects.equals(this.clientContacts, client.clientContacts) &&
        Objects.equals(this.createdAt, client.createdAt) &&
        Objects.equals(this.updatedAt, client.updatedAt) &&
        Objects.equals(this.clientLogo, client.clientLogo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, clientId, clientName, clientShortName, clientSecretExportSettings, institutionMetainfo, redirectUris, postLogoutRedirectUris, scope, secret, description, infoNotificationEmails, slaNotificationEmails, isUserConsentRequired, clientUrl, backchannelLogoutUri, midSettings, smartidSettings, clientContacts, createdAt, updatedAt, clientLogo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Client {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    clientId: ").append(toIndentedString(clientId)).append("\n");
    sb.append("    clientName: ").append(toIndentedString(clientName)).append("\n");
    sb.append("    clientShortName: ").append(toIndentedString(clientShortName)).append("\n");
    sb.append("    clientSecretExportSettings: ").append(toIndentedString(clientSecretExportSettings)).append("\n");
    sb.append("    institutionMetainfo: ").append(toIndentedString(institutionMetainfo)).append("\n");
    sb.append("    redirectUris: ").append(toIndentedString(redirectUris)).append("\n");
    sb.append("    postLogoutRedirectUris: ").append(toIndentedString(postLogoutRedirectUris)).append("\n");
    sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
    sb.append("    secret: ").append(toIndentedString(secret)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    infoNotificationEmails: ").append(toIndentedString(infoNotificationEmails)).append("\n");
    sb.append("    slaNotificationEmails: ").append(toIndentedString(slaNotificationEmails)).append("\n");
    sb.append("    isUserConsentRequired: ").append(toIndentedString(isUserConsentRequired)).append("\n");
    sb.append("    clientUrl: ").append(toIndentedString(clientUrl)).append("\n");
    sb.append("    backchannelLogoutUri: ").append(toIndentedString(backchannelLogoutUri)).append("\n");
    sb.append("    midSettings: ").append(toIndentedString(midSettings)).append("\n");
    sb.append("    smartidSettings: ").append(toIndentedString(smartidSettings)).append("\n");
    sb.append("    clientContacts: ").append(toIndentedString(clientContacts)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    clientLogo: ").append(toIndentedString(clientLogo)).append("\n");
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

