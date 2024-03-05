package ee.ria.tara.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import ee.ria.tara.model.ClientContact;
import ee.ria.tara.model.ClientMidSettings;
import ee.ria.tara.model.ClientSecretExportSettings;
import ee.ria.tara.model.ClientSmartIdSettings;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.model.NameTranslations;
import ee.ria.tara.model.ShortNameTranslations;
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
 * Client
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class Client {

  private String id;

  private String clientId;

  private NameTranslations clientName;

  private ShortNameTranslations clientShortName;

  private ClientSecretExportSettings clientSecretExportSettings;

  private InstitutionMetainfo institutionMetainfo;

  @Valid
  private List<String> accessTokenAudienceUris;

  @Valid
  private List<String> redirectUris = new ArrayList<>();

  @Valid
  private List<String> postLogoutRedirectUris;

  @Valid
  private List<@Size(min = 2, max = 200)String> scope = new ArrayList<>();

  @Valid
  private List<@Size(min = 1, max = 200)String> tokenRequestAllowedIpAddresses = new ArrayList<>();

  /**
   * Gets or Sets tokenEndpointAuthMethod
   */
  public enum TokenEndpointAuthMethodEnum {
    BASIC("client_secret_basic"),
    
    POST("client_secret_post");

    private String value;

    TokenEndpointAuthMethodEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TokenEndpointAuthMethodEnum fromValue(String value) {
      for (TokenEndpointAuthMethodEnum b : TokenEndpointAuthMethodEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TokenEndpointAuthMethodEnum tokenEndpointAuthMethod;

  private String secret;

  private String eidasRequesterId;

  private String description;

  @Valid
  private List<@Email String> infoNotificationEmails;

  @Valid
  private List<@Email String> slaNotificationEmails;

  private Boolean accessTokenJwtEnabled = false;

  private Boolean isUserConsentRequired;

  @Valid
  private List<String> skipUserConsentClientIds;

  private String clientUrl;

  private String backchannelLogoutUri;

  private String paasukeParameters;

  private ClientMidSettings midSettings;

  private ClientSmartIdSettings smartidSettings;

  @Valid
  private List<@Valid ClientContact> clientContacts;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  private byte[] clientLogo;

  public Client() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Client(String clientId, InstitutionMetainfo institutionMetainfo, List<String> redirectUris, List<@Size(min = 2, max = 200)String> scope, List<@Size(min = 1, max = 200)String> tokenRequestAllowedIpAddresses, TokenEndpointAuthMethodEnum tokenEndpointAuthMethod) {
    this.clientId = clientId;
    this.institutionMetainfo = institutionMetainfo;
    this.redirectUris = redirectUris;
    this.scope = scope;
    this.tokenRequestAllowedIpAddresses = tokenRequestAllowedIpAddresses;
    this.tokenEndpointAuthMethod = tokenEndpointAuthMethod;
  }

  public Client id(String id) {
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

  public Client clientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

  /**
   * Get clientId
   * @return clientId
  */
  @NotNull @Pattern(regexp = "^(?!\\s*$).+") @Size(min = 3, max = 255) 
  @Schema(name = "client_id", example = "openIdDemo", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("client_id")
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
  @Valid 
  @Schema(name = "client_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_name")
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
  @Valid 
  @Schema(name = "client_short_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_short_name")
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
  @Valid 
  @Schema(name = "client_secret_export_settings", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_secret_export_settings")
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
  @NotNull @Valid 
  @Schema(name = "institution_metainfo", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("institution_metainfo")
  public InstitutionMetainfo getInstitutionMetainfo() {
    return institutionMetainfo;
  }

  public void setInstitutionMetainfo(InstitutionMetainfo institutionMetainfo) {
    this.institutionMetainfo = institutionMetainfo;
  }

  public Client accessTokenAudienceUris(List<String> accessTokenAudienceUris) {
    this.accessTokenAudienceUris = accessTokenAudienceUris;
    return this;
  }

  public Client addAccessTokenAudienceUrisItem(String accessTokenAudienceUrisItem) {
    if (this.accessTokenAudienceUris == null) {
      this.accessTokenAudienceUris = new ArrayList<>();
    }
    this.accessTokenAudienceUris.add(accessTokenAudienceUrisItem);
    return this;
  }

  /**
   * Get accessTokenAudienceUris
   * @return accessTokenAudienceUris
  */
  
  @Schema(name = "access_token_audience_uris", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("access_token_audience_uris")
  public List<String> getAccessTokenAudienceUris() {
    return accessTokenAudienceUris;
  }

  public void setAccessTokenAudienceUris(List<String> accessTokenAudienceUris) {
    this.accessTokenAudienceUris = accessTokenAudienceUris;
  }

  public Client redirectUris(List<String> redirectUris) {
    this.redirectUris = redirectUris;
    return this;
  }

  public Client addRedirectUrisItem(String redirectUrisItem) {
    if (this.redirectUris == null) {
      this.redirectUris = new ArrayList<>();
    }
    this.redirectUris.add(redirectUrisItem);
    return this;
  }

  /**
   * Get redirectUris
   * @return redirectUris
  */
  @NotNull @Size(min = 1) 
  @Schema(name = "redirect_uris", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("redirect_uris")
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
    if (this.postLogoutRedirectUris == null) {
      this.postLogoutRedirectUris = new ArrayList<>();
    }
    this.postLogoutRedirectUris.add(postLogoutRedirectUrisItem);
    return this;
  }

  /**
   * Get postLogoutRedirectUris
   * @return postLogoutRedirectUris
  */
  @Size(min = 1) 
  @Schema(name = "post_logout_redirect_uris", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("post_logout_redirect_uris")
  public List<String> getPostLogoutRedirectUris() {
    return postLogoutRedirectUris;
  }

  public void setPostLogoutRedirectUris(List<String> postLogoutRedirectUris) {
    this.postLogoutRedirectUris = postLogoutRedirectUris;
  }

  public Client scope(List<@Size(min = 2, max = 200)String> scope) {
    this.scope = scope;
    return this;
  }

  public Client addScopeItem(String scopeItem) {
    if (this.scope == null) {
      this.scope = new ArrayList<>();
    }
    this.scope.add(scopeItem);
    return this;
  }

  /**
   * Get scope
   * @return scope
  */
  @NotNull @Size(min = 1) 
  @Schema(name = "scope", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("scope")
  public List<@Size(min = 2, max = 200)String> getScope() {
    return scope;
  }

  public void setScope(List<@Size(min = 2, max = 200)String> scope) {
    this.scope = scope;
  }

  public Client tokenRequestAllowedIpAddresses(List<@Size(min = 1, max = 200)String> tokenRequestAllowedIpAddresses) {
    this.tokenRequestAllowedIpAddresses = tokenRequestAllowedIpAddresses;
    return this;
  }

  public Client addTokenRequestAllowedIpAddressesItem(String tokenRequestAllowedIpAddressesItem) {
    if (this.tokenRequestAllowedIpAddresses == null) {
      this.tokenRequestAllowedIpAddresses = new ArrayList<>();
    }
    this.tokenRequestAllowedIpAddresses.add(tokenRequestAllowedIpAddressesItem);
    return this;
  }

  /**
   * Get tokenRequestAllowedIpAddresses
   * @return tokenRequestAllowedIpAddresses
  */
  @NotNull @Size(min = 1) 
  @Schema(name = "token_request_allowed_ip_addresses", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("token_request_allowed_ip_addresses")
  public List<@Size(min = 1, max = 200)String> getTokenRequestAllowedIpAddresses() {
    return tokenRequestAllowedIpAddresses;
  }

  public void setTokenRequestAllowedIpAddresses(List<@Size(min = 1, max = 200)String> tokenRequestAllowedIpAddresses) {
    this.tokenRequestAllowedIpAddresses = tokenRequestAllowedIpAddresses;
  }

  public Client tokenEndpointAuthMethod(TokenEndpointAuthMethodEnum tokenEndpointAuthMethod) {
    this.tokenEndpointAuthMethod = tokenEndpointAuthMethod;
    return this;
  }

  /**
   * Get tokenEndpointAuthMethod
   * @return tokenEndpointAuthMethod
  */
  @NotNull 
  @Schema(name = "token_endpoint_auth_method", example = "client_secret_basic", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("token_endpoint_auth_method")
  public TokenEndpointAuthMethodEnum getTokenEndpointAuthMethod() {
    return tokenEndpointAuthMethod;
  }

  public void setTokenEndpointAuthMethod(TokenEndpointAuthMethodEnum tokenEndpointAuthMethod) {
    this.tokenEndpointAuthMethod = tokenEndpointAuthMethod;
  }

  public Client secret(String secret) {
    this.secret = secret;
    return this;
  }

  /**
   * Get secret
   * @return secret
  */
  
  @Schema(name = "secret", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("secret")
  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public Client eidasRequesterId(String eidasRequesterId) {
    this.eidasRequesterId = eidasRequesterId;
    return this;
  }

  /**
   * Get eidasRequesterId
   * @return eidasRequesterId
  */
  @Pattern(regexp = "^((?!urn:uuid:)[a-zA-Z][a-zA-Z0-9+.-]*:.*|urn:uuid:[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})$") @Size(max = 1024) 
  @Schema(name = "eidas_requester_id", example = "urn:uuid:33ca0ae1-a5fb-4885-80d7-6af6bf6e0e5f", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("eidas_requester_id")
  public String getEidasRequesterId() {
    return eidasRequesterId;
  }

  public void setEidasRequesterId(String eidasRequesterId) {
    this.eidasRequesterId = eidasRequesterId;
  }

  public Client description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
  */
  @Size(min = 3, max = 4000) 
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Client infoNotificationEmails(List<@Email String> infoNotificationEmails) {
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
  
  @Schema(name = "info_notification_emails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("info_notification_emails")
  public List<@Email String> getInfoNotificationEmails() {
    return infoNotificationEmails;
  }

  public void setInfoNotificationEmails(List<@Email String> infoNotificationEmails) {
    this.infoNotificationEmails = infoNotificationEmails;
  }

  public Client slaNotificationEmails(List<@Email String> slaNotificationEmails) {
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
  
  @Schema(name = "sla_notification_emails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sla_notification_emails")
  public List<@Email String> getSlaNotificationEmails() {
    return slaNotificationEmails;
  }

  public void setSlaNotificationEmails(List<@Email String> slaNotificationEmails) {
    this.slaNotificationEmails = slaNotificationEmails;
  }

  public Client accessTokenJwtEnabled(Boolean accessTokenJwtEnabled) {
    this.accessTokenJwtEnabled = accessTokenJwtEnabled;
    return this;
  }

  /**
   * Get accessTokenJwtEnabled
   * @return accessTokenJwtEnabled
  */
  
  @Schema(name = "access_token_jwt_enabled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("access_token_jwt_enabled")
  public Boolean getAccessTokenJwtEnabled() {
    return accessTokenJwtEnabled;
  }

  public void setAccessTokenJwtEnabled(Boolean accessTokenJwtEnabled) {
    this.accessTokenJwtEnabled = accessTokenJwtEnabled;
  }

  public Client isUserConsentRequired(Boolean isUserConsentRequired) {
    this.isUserConsentRequired = isUserConsentRequired;
    return this;
  }

  /**
   * Get isUserConsentRequired
   * @return isUserConsentRequired
  */
  
  @Schema(name = "is_user_consent_required", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("is_user_consent_required")
  public Boolean getIsUserConsentRequired() {
    return isUserConsentRequired;
  }

  public void setIsUserConsentRequired(Boolean isUserConsentRequired) {
    this.isUserConsentRequired = isUserConsentRequired;
  }

  public Client skipUserConsentClientIds(List<String> skipUserConsentClientIds) {
    this.skipUserConsentClientIds = skipUserConsentClientIds;
    return this;
  }

  public Client addSkipUserConsentClientIdsItem(String skipUserConsentClientIdsItem) {
    if (this.skipUserConsentClientIds == null) {
      this.skipUserConsentClientIds = new ArrayList<>();
    }
    this.skipUserConsentClientIds.add(skipUserConsentClientIdsItem);
    return this;
  }

  /**
   * Get skipUserConsentClientIds
   * @return skipUserConsentClientIds
  */
  
  @Schema(name = "skip_user_consent_client_ids", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("skip_user_consent_client_ids")
  public List<String> getSkipUserConsentClientIds() {
    return skipUserConsentClientIds;
  }

  public void setSkipUserConsentClientIds(List<String> skipUserConsentClientIds) {
    this.skipUserConsentClientIds = skipUserConsentClientIds;
  }

  public Client clientUrl(String clientUrl) {
    this.clientUrl = clientUrl;
    return this;
  }

  /**
   * Get clientUrl
   * @return clientUrl
  */
  
  @Schema(name = "client_url", example = "https://client.example.com/", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_url")
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
  
  @Schema(name = "backchannel_logout_uri", example = "https://example.com/", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("backchannel_logout_uri")
  public String getBackchannelLogoutUri() {
    return backchannelLogoutUri;
  }

  public void setBackchannelLogoutUri(String backchannelLogoutUri) {
    this.backchannelLogoutUri = backchannelLogoutUri;
  }

  public Client paasukeParameters(String paasukeParameters) {
    this.paasukeParameters = paasukeParameters;
    return this;
  }

  /**
   * Get paasukeParameters
   * @return paasukeParameters
  */
  
  @Schema(name = "paasuke_parameters", example = "ns=A&role=B", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paasuke_parameters")
  public String getPaasukeParameters() {
    return paasukeParameters;
  }

  public void setPaasukeParameters(String paasukeParameters) {
    this.paasukeParameters = paasukeParameters;
  }

  public Client midSettings(ClientMidSettings midSettings) {
    this.midSettings = midSettings;
    return this;
  }

  /**
   * Get midSettings
   * @return midSettings
  */
  @Valid 
  @Schema(name = "mid_settings", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mid_settings")
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
  @Valid 
  @Schema(name = "smartid_settings", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("smartid_settings")
  public ClientSmartIdSettings getSmartidSettings() {
    return smartidSettings;
  }

  public void setSmartidSettings(ClientSmartIdSettings smartidSettings) {
    this.smartidSettings = smartidSettings;
  }

  public Client clientContacts(List<@Valid ClientContact> clientContacts) {
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
  @Valid 
  @Schema(name = "client_contacts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_contacts")
  public List<@Valid ClientContact> getClientContacts() {
    return clientContacts;
  }

  public void setClientContacts(List<@Valid ClientContact> clientContacts) {
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
  @Valid 
  @Schema(name = "created_at", example = "2019-08-24T14:15:22Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("created_at")
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
  @Valid 
  @Schema(name = "updated_at", example = "2019-08-24T14:15:22Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updated_at")
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
  
  @Schema(name = "client_logo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_logo")
  public byte[] getClientLogo() {
    return clientLogo;
  }

  public void setClientLogo(byte[] clientLogo) {
    this.clientLogo = clientLogo;
  }

  @Override
  public boolean equals(Object o) {
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
        Objects.equals(this.accessTokenAudienceUris, client.accessTokenAudienceUris) &&
        Objects.equals(this.redirectUris, client.redirectUris) &&
        Objects.equals(this.postLogoutRedirectUris, client.postLogoutRedirectUris) &&
        Objects.equals(this.scope, client.scope) &&
        Objects.equals(this.tokenRequestAllowedIpAddresses, client.tokenRequestAllowedIpAddresses) &&
        Objects.equals(this.tokenEndpointAuthMethod, client.tokenEndpointAuthMethod) &&
        Objects.equals(this.secret, client.secret) &&
        Objects.equals(this.eidasRequesterId, client.eidasRequesterId) &&
        Objects.equals(this.description, client.description) &&
        Objects.equals(this.infoNotificationEmails, client.infoNotificationEmails) &&
        Objects.equals(this.slaNotificationEmails, client.slaNotificationEmails) &&
        Objects.equals(this.accessTokenJwtEnabled, client.accessTokenJwtEnabled) &&
        Objects.equals(this.isUserConsentRequired, client.isUserConsentRequired) &&
        Objects.equals(this.skipUserConsentClientIds, client.skipUserConsentClientIds) &&
        Objects.equals(this.clientUrl, client.clientUrl) &&
        Objects.equals(this.backchannelLogoutUri, client.backchannelLogoutUri) &&
        Objects.equals(this.paasukeParameters, client.paasukeParameters) &&
        Objects.equals(this.midSettings, client.midSettings) &&
        Objects.equals(this.smartidSettings, client.smartidSettings) &&
        Objects.equals(this.clientContacts, client.clientContacts) &&
        Objects.equals(this.createdAt, client.createdAt) &&
        Objects.equals(this.updatedAt, client.updatedAt) &&
        Arrays.equals(this.clientLogo, client.clientLogo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, clientId, clientName, clientShortName, clientSecretExportSettings, institutionMetainfo, accessTokenAudienceUris, redirectUris, postLogoutRedirectUris, scope, tokenRequestAllowedIpAddresses, tokenEndpointAuthMethod, secret, eidasRequesterId, description, infoNotificationEmails, slaNotificationEmails, accessTokenJwtEnabled, isUserConsentRequired, skipUserConsentClientIds, clientUrl, backchannelLogoutUri, paasukeParameters, midSettings, smartidSettings, clientContacts, createdAt, updatedAt, Arrays.hashCode(clientLogo));
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
    sb.append("    accessTokenAudienceUris: ").append(toIndentedString(accessTokenAudienceUris)).append("\n");
    sb.append("    redirectUris: ").append(toIndentedString(redirectUris)).append("\n");
    sb.append("    postLogoutRedirectUris: ").append(toIndentedString(postLogoutRedirectUris)).append("\n");
    sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
    sb.append("    tokenRequestAllowedIpAddresses: ").append(toIndentedString(tokenRequestAllowedIpAddresses)).append("\n");
    sb.append("    tokenEndpointAuthMethod: ").append(toIndentedString(tokenEndpointAuthMethod)).append("\n");
    sb.append("    secret: ").append(toIndentedString(secret)).append("\n");
    sb.append("    eidasRequesterId: ").append(toIndentedString(eidasRequesterId)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    infoNotificationEmails: ").append(toIndentedString(infoNotificationEmails)).append("\n");
    sb.append("    slaNotificationEmails: ").append(toIndentedString(slaNotificationEmails)).append("\n");
    sb.append("    accessTokenJwtEnabled: ").append(toIndentedString(accessTokenJwtEnabled)).append("\n");
    sb.append("    isUserConsentRequired: ").append(toIndentedString(isUserConsentRequired)).append("\n");
    sb.append("    skipUserConsentClientIds: ").append(toIndentedString(skipUserConsentClientIds)).append("\n");
    sb.append("    clientUrl: ").append(toIndentedString(clientUrl)).append("\n");
    sb.append("    backchannelLogoutUri: ").append(toIndentedString(backchannelLogoutUri)).append("\n");
    sb.append("    paasukeParameters: ").append(toIndentedString(paasukeParameters)).append("\n");
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
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

