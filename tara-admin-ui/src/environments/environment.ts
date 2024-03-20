// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  // https://github.com/angular/angular/issues/20511#issuecomment-430672830
  backendUrl: "//localhost:8080",
  clientScopes: ["openid", "idcard", "mid", "smartid", "eidas", "eidasonly", "eidas:country:*", "email", "phone", "legalperson"],
  ssoClientScopes: ["openid", "email", "phone", "representee"],
  alertScopes: ["idcard", "mid", "smartid", "eidas"],
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
