export const environment = {
  production: true,
  backendUrl: '//' + window.location.hostname + ':' + window.location.port,
  clientScopes: ["openid", "idcard", "mid", "smartid", "eidas", "eidasonly", "eidas:country:*", "email", "phone", "legalperson"],
  ssoClientScopes: ["openid", "email", "phone"],
  alertScopes: ["idcard", "mid", "smartid", "eidas"],
  institutionType: {public: "Avalik-õiguslik juriidiline isik", private: "Eraõiguslik juriidiline isik"},
  errorMessageDurationInMills: 10000,
  successMessageDurationInMills: 5000,
  infoMessageDurationInMills: 5000,
};
