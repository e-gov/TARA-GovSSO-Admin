export type Alert = {
  id?: string,
  title: string,
  start_time: string,
  end_time: string,
  login_alert: LoginAlert,
  email_alert?: EmailAlert,
  created_at?: string,
  updated_at?: string
}

export type LoginAlert = {
  enabled: boolean,
  auth_methods: string[],
  message_templates: MessageTemplate[]
}

export type EmailAlert = {
  enabled: boolean,
  send_at: string,
  message_templates: MessageTemplate[]
}

export type MessageTemplate = {
  message: string,
  locale: string
}
