export type Institution = {
  id?: number,
  registry_code: string | undefined,
  name: string | undefined,
  type: InstitutionType,
  client_ids: string[],
  address: string | undefined,
  phone: string | undefined,
  email: string | undefined,
  billing_settings: InstitutionBillingSettings,
  created_at?: string,
  updated_at?: string
}

export type InstitutionBillingSettings = {
  email: string | undefined
}

export type InstitutionType = {
  type: string | undefined
}
