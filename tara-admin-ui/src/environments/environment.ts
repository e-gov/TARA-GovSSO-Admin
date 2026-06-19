// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import {
  SCOPE_EIDAS, SCOPE_EIDAS_COUNTRY, SCOPE_EIDAS_ONLY, SCOPE_EMAIL, SCOPE_IDCARD, SCOPE_LEGALPERSON,
  SCOPE_MID, SCOPE_OPENID, SCOPE_PHONE, SCOPE_REPRESENTEE, SCOPE_REPRESENTEE_LIST,
  SCOPE_AUTH_HANDOVER, SCOPE_SMARTID,
} from '../app/clients/model/scopes';

export const environment = {
  production: false,
  // https://github.com/angular/angular/issues/20511#issuecomment-430672830
  backendUrl: "//localhost:8080",
  clientScopes: [SCOPE_OPENID, SCOPE_IDCARD, SCOPE_MID, SCOPE_SMARTID, SCOPE_EIDAS, SCOPE_EIDAS_ONLY, SCOPE_EIDAS_COUNTRY, SCOPE_EMAIL, SCOPE_PHONE, SCOPE_LEGALPERSON],
  ssoClientScopes: [SCOPE_OPENID, SCOPE_EMAIL, SCOPE_PHONE, SCOPE_REPRESENTEE, SCOPE_REPRESENTEE_LIST, SCOPE_AUTH_HANDOVER],
  alertScopes: [SCOPE_IDCARD, SCOPE_MID, SCOPE_SMARTID, SCOPE_EIDAS],
  institutionType: {public: "Avalik-õiguslik juriidiline isik", private: "Eraõiguslik juriidiline isik"},
  errorMessageDurationInMills: 10000,
  successMessageDurationInMills: 5000,
  infoMessageDurationInMills: 5000,
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
