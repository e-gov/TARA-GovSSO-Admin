export type Client = {
  institution_metainfo: InstitutionMetainfo,
  client_id: string | undefined,
  client_name: ClientNameTranslation,
  client_short_name: ClientNameTranslation,
  client_secret_export_settings?: ClientSecretExportSettings,
  redirect_uris: string[],
  post_logout_redirect_uris: string[],
  scope: string[],
  is_user_consent_required: boolean,
  client_url: string | undefined,
  info_notification_emails: string[],
  sla_notification_emails: string[],
  created_at: string | undefined,
  updated_at: string | undefined,
  smartid_settings: ClientSmartIdSettings,
  client_contacts: ClientContact[],
  mid_settings: ClientMidSettings,
  description: string | undefined,
  client_logo: string | undefined,
  backchannel_logout_uri: string | undefined
}

export type ClientContact = {
  name: string | undefined,
  email: string | undefined,
  phone: string | undefined,
  department: string | undefined
}

export type ClientNameTranslation = {
  et: string | undefined,
  en: string | undefined,
  ru: string | undefined
}

export type ClientSmartIdSettings = {
  relying_party_UUID: string | undefined,
  relying_party_name: string | undefined,
  should_use_additional_verification_code_check: boolean
}

export type ClientMidSettings = {
  relying_party_UUID: string | undefined,
  relying_party_name: string | undefined
}

export type ClientSecretExportSettings = {
  recipient_id_code: string | undefined,
  recipient_name_in_ldap: string | undefined,
  recipient_email: string | undefined
}

export type InstitutionMetainfo = {
  registry_code: string | undefined,
  name: string | undefined,
  type: {
    type: "public" | "private"
  }
}
