import {
  SCOPE_EIDAS, SCOPE_EIDAS_COUNTRY, SCOPE_EIDAS_ONLY, SCOPE_EMAIL, SCOPE_IDCARD, SCOPE_LEGALPERSON,
  SCOPE_MID, SCOPE_OPENID, SCOPE_PHONE, SCOPE_REPRESENTEE, SCOPE_REPRESENTEE_LIST,
  SCOPE_AUTH_HANDOVER, SCOPE_SMARTID,
} from '../app/clients/model/scopes';

export const environment = {
  production: true,
  backendUrl: '//' + window.location.hostname + ':' + window.location.port,
  clientScopes: [SCOPE_OPENID, SCOPE_IDCARD, SCOPE_MID, SCOPE_SMARTID, SCOPE_EIDAS, SCOPE_EIDAS_ONLY, SCOPE_EIDAS_COUNTRY, SCOPE_EMAIL, SCOPE_PHONE, SCOPE_LEGALPERSON],
  ssoClientScopes: [SCOPE_OPENID, SCOPE_EMAIL, SCOPE_PHONE, SCOPE_REPRESENTEE, SCOPE_REPRESENTEE_LIST, SCOPE_AUTH_HANDOVER],
  alertScopes: [SCOPE_IDCARD, SCOPE_MID, SCOPE_SMARTID, SCOPE_EIDAS],
  institutionType: {public: "Avalik-õiguslik juriidiline isik", private: "Eraõiguslik juriidiline isik"},
  errorMessageDurationInMills: 10000,
  successMessageDurationInMills: 5000,
  infoMessageDurationInMills: 5000,
};
