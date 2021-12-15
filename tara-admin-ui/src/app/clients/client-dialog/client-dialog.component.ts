import {Component, Inject, OnInit} from '@angular/core';
// @ts-ignore
import {
  Client,
  ClientMidSettings,
  ClientNameTranslation,
  ClientSmartIdSettings,
  ClientContact,
  InstitutionMetainfo
} from "../model/client";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {AddValueDialogComponent} from "./add-value-dialog/add-value-dialog.component";
import {environment} from "../../../environments/environment";
import {AddTranslationDialogComponent} from "./add-translation-dialog/add-translation-dialog.component";
import {AddContactDialogComponent} from "./add-contact-dialog/add-contact-dialog.component";
import {ChangeSecretDialogComponent} from "./change-secret-dialog/change-secret-dialog.component";
import {MatCheckboxChange} from "@angular/material/checkbox";
import {VerifyComponent} from "../../main/verify/verify.component";
import {Observable} from "rxjs";
import {DateHelper} from "../../helper/datehelper";
import {AuthService} from "../../auth/auth.service";
import {EditValueDialogComponent} from "./edit-value-dialog/edit-value-dialog.component";

@Component({
  selector: 'app-client-dialog',
  templateUrl: './client-dialog.component.html',
  styleUrls: ['./client-dialog.component.css']
})
export class ClientDialogComponent implements OnInit {
  _client_id?: string;
  _client_name?: ClientNameTranslation;
  _client_short_name?: ClientNameTranslation;
  _client_url?: string;
  _mid_settings?: ClientMidSettings;
  _smartid_settings?: ClientSmartIdSettings;
  _description?: string;
  _use_specific_mid_configuration: boolean;
  _use_specific_smartid_configuration: boolean;
  _client_logo?: string;
  _backchannel_logout_uri: string;

  clientLogoDataUri?: string;
  newData: Client;

  constructor(@Inject(MAT_DIALOG_DATA) public data: {
                dialogType: "ADD" | "UPDATE" | "INFO"
                obj: Client,
                institutions: Observable<InstitutionMetainfo[]>,
                onTypeAction: (type: any, client: Client) => Promise<any>;
              },
              public mainDialog: MatDialogRef<ClientDialogComponent>,
              public dialog: MatDialog,
              public authService: AuthService) {
    this.newData = this.validateAndGetNewClient(this.data.obj)

    this._client_name = this.newData.client_name;
    this._client_short_name = this.newData.client_short_name;
    this._description = this.newData.description;
    this._client_id = this.newData.client_id;
    this._client_url = this.newData.client_url;
    this._smartid_settings = this.newData.smartid_settings;
    this._mid_settings = this.newData.mid_settings;
    this._client_logo = this.newData.client_logo;
    this._backchannel_logout_uri = this.newData.backchannel_logout_uri;

    if (this._client_logo !== undefined) {
      this.clientLogoDataUri = "data:image/png;base64,"+this._client_logo;
    }

    this._use_specific_smartid_configuration = this.newData.smartid_settings.relying_party_UUID != undefined ||
      this.newData.smartid_settings.relying_party_name != undefined;
    this._use_specific_mid_configuration = this.newData.mid_settings.relying_party_UUID != undefined ||
      this.newData.mid_settings.relying_party_name != undefined;
  }

  compareInstitutionMetainfo(o1: InstitutionMetainfo, o2: InstitutionMetainfo): boolean {
    return o1.registry_code == o2.registry_code && o1.type.type == o2.type.type && o1.name == o2.name;
  }

  ngOnInit(): void {
    this.mainDialog.backdropClick().subscribe(() => {
      this.mainDialog.close();
    });
  }

  ngAfterViewInit(): void {
    if (this._client_logo !== undefined) {
      document.getElementById("client-logo-file").removeAttribute('required');
    }
  }

  isInfo(): boolean {
    return !['ADD', 'UPDATE'].includes(this.data.dialogType);
  }

  onImageChange(e: any) {

    const reader = new FileReader();

      if(e.target.files && e.target.files.length) {
        const [file] = e.target.files;
        reader.readAsDataURL(file);

        reader.onload = () => {
          var image = reader.result as string;

          var base64Index = image.indexOf(';base64,') + ';base64,'.length;
          var base64 = image.substring(base64Index);

          if (!image.includes('image/png') && !image.includes('image/jpeg')) {
              document.getElementById("client-logo-alert").style.visibility = 'visible';
            } else {
              document.getElementById("client-logo-alert").style.visibility = 'hidden';
              this.clientLogoDataUri = image;
              this._client_logo = base64;
            }

        };
      }
  }

  openRedirectUrlDialog(): void {
    const dialogRef = this.dialog.open(AddValueDialogComponent, {
      data: {
        title: "Lubatud tagasipöördumise URL-i lisamine"
      },
      backdropClass: 'cdk-overlay-transparent-backdrop',
      hasBackdrop: true,
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result !== "" && result !== undefined) {
        this.newData.redirect_uris.push(result);
      }
    });
  }

  openLogoutRedirectUrlDialog(): void {
      const dialogRef = this.dialog.open(AddValueDialogComponent, {
        data: {
          title: "Lubatud tagasipöördumise URL-i lisamine"
        },
        backdropClass: 'cdk-overlay-transparent-backdrop',
        hasBackdrop: true,
      });

      dialogRef.afterClosed().subscribe((result: any) => {
        if (result !== "" && result !== undefined) {
          this.newData.post_logout_redirect_uris.push(result);
        }
      });
    }

  openSecretDialog(): void {
    const dialogRef = this.dialog.open(ChangeSecretDialogComponent, {
        width: "600px",
        data: this.newData.client_secret_export_settings,
        backdropClass: 'cdk-overlay-transparent-backdrop',
        hasBackdrop: true
      }
    );

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result !== "" && result !== undefined) {
        this.newData.client_secret_export_settings = result;
      }
    });
  }

  openInfoNotificationEmailDialog(): void {
    const dialogRef = this.dialog.open(AddValueDialogComponent, {
      data: {
        title: "Tehnilise teavituse E-maili lisamine"
      },
      backdropClass: 'cdk-overlay-transparent-backdrop',
      hasBackdrop: true,
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result !== "" && result !== undefined) {
        this.newData.info_notification_emails.push(result);
      }
    });
  }

  openSlaNotificationEmailDialog(): void {
    const dialogRef = this.dialog.open(AddValueDialogComponent, {
      data: {
        title: "Katkestustest teavitamise E-maili lisamine"
      },
      backdropClass: 'cdk-overlay-transparent-backdrop',
      hasBackdrop: true,
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result !== "" && result !== undefined) {
        this.newData.sla_notification_emails.push(result);
      }
    });
  }

  getPossibleScopes(): string[] {
    return environment.clientScopes;
  }

  removeRedirectUri(uri: string) {
    this.newData.redirect_uris.splice(this.newData.redirect_uris.indexOf(uri), 1)
  }

  removeLogoutRedirectUri(uri: string) {
    this.newData.post_logout_redirect_uris.splice(this.newData.post_logout_redirect_uris.indexOf(uri), 1)
  }

  removeContact(contact: ClientContact) {
   this.newData.client_contacts.splice(this.newData.client_contacts.indexOf(contact), 1)
  }

  save() {
    this.newData.client_name = this._client_name!;
    this.newData.client_short_name = this._client_short_name!;
    this.newData.description = this._description!;
    this.newData.client_id = this._client_id!;
    this.newData.client_url = this._client_url!;
    this.newData.client_logo = this._client_logo!;
    this.newData.backchannel_logout_uri = this._backchannel_logout_uri!;

    if (this.newData.scope.includes("smartid") && this._use_specific_smartid_configuration) {
      this.newData.smartid_settings = this._smartid_settings!
    } else {
      this.newData.smartid_settings = {
        relying_party_UUID: undefined,
        relying_party_name: undefined,
        should_use_additional_verification_code_check: this._smartid_settings!.should_use_additional_verification_code_check
      };
    }

    if (this.newData.scope.includes("mid") && this._use_specific_mid_configuration) {
      this.newData.mid_settings = this._mid_settings!;
    } else {
      this.newData.mid_settings = {
        relying_party_UUID: undefined,
        relying_party_name: undefined
      };
    }

    let requestBody = JSON.parse(JSON.stringify(this.newData), (key, value) => {
       if (value == '')
           return undefined;
       return value;
    });

    this.data.onTypeAction(this.data.dialogType, requestBody).then(() => {
      this.mainDialog.close();
    });
  }

  removeSupportEmail(email: string) {
    this.newData.info_notification_emails.splice(this.newData.info_notification_emails.indexOf(email), 1)
  }

  removeBlackoutEmail(email: string) {
    this.newData.sla_notification_emails.splice(this.newData.sla_notification_emails.indexOf(email), 1)
  }

  addTranslation(data: ClientNameTranslation) {
    const dialogRef = this.dialog.open(AddTranslationDialogComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      hasBackdrop: true,
      width: "400px",
      data: data
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result !== "" && result !== undefined) {
        data.et = result.et;
        data.en = result.en;
        data.ru = result.ru;
      }
    });
  }

  updateContact(data: ClientContact) {
    const dialogRef = this.dialog.open(AddContactDialogComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      hasBackdrop: true,
      width: "500px",
      data: { dialogType: 'UPDATE', contact: data}
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result !== "" && result !== undefined) {
        data.name = result.name;
        data.email = result.email;
        data.phone = result.phone;
        data.department = result.department;
      }
    });
  }

  addContact() {
    const dialogRef = this.dialog.open(AddContactDialogComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      hasBackdrop: true,
      width: "500px",
      data: { dialogType: 'ADD', contact: {}}
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result !== "" && result !== undefined) {
        this.newData.client_contacts.push(result);
      }
    });
  }

  onScopeChange(event: MatCheckboxChange) {
    if (event.checked) {
      this.newData.scope.push(event.source.name!)
    } else {
      this.newData.scope = this.newData.scope.filter((val: string) => val !== event.source.name)
    }
  }

  delete() {
    const dialogRef = this.dialog.open(VerifyComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      hasBackdrop: true,
      width: "400px",
      data: "Kas oled kindel, et soovid klientrakenduse kustutada?"
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result === true) {
        this.data.onTypeAction(this.data.dialogType, this.data.obj).then(() => {
          this.mainDialog.close();
        });
      }
    });
  }

  validateAndGetNewClient(client: Client): Client {
    let newClient = JSON.parse(JSON.stringify(client));

    if (newClient.institution_metainfo == undefined) {
      newClient.institution_metainfo = {type: "private"};
    }

    if (newClient.client_name == undefined) {
      newClient.client_name = {};
    }

    if (newClient.client_short_name == undefined) {
      newClient.client_short_name = {};
    }

    if (newClient.client_secret_export_settings == undefined && this.data.dialogType === 'ADD') {
      newClient.client_secret_export_settings = {}
    }

    if (newClient.smartid_settings == undefined) {
      newClient.smartid_settings = {};
    }

    if (newClient.mid_settings == undefined) {
      newClient.mid_settings = {};
    }

    if (newClient.info_notification_emails == undefined) {
      newClient.info_notification_emails = [];
    }

    if (newClient.sla_notification_emails == undefined) {
      newClient.sla_notification_emails = [];
    }

    if (newClient.client_contacts == undefined) {
      newClient.client_contacts = [];
    }

    return newClient;
  }

  getDisplayableDateTime(dateTime: string) {
    return DateHelper.convertToDisplayString(dateTime);
  }

  cancel() {
    this.mainDialog.close();
  }

  openEditRedirectUrlDialog(url: string) {
    const dialogRef = this.dialog.open(EditValueDialogComponent, {
      data: {
        title: "Muuda autentimise tagasisuunasmispäringu URL-i",
        url: url
      },
      backdropClass: 'cdk-overlay-transparent-backdrop',
      hasBackdrop: true,
    });

    dialogRef.afterClosed().subscribe(({newUrl, oldUrl}) => {
      if (newUrl !== "" && newUrl !== undefined && oldUrl !== "" && oldUrl !== undefined) {
        const index = this.newData.redirect_uris.indexOf(oldUrl);
        this.newData.redirect_uris[index] = newUrl;
      }
    });
  }

  openEditLogoutRedirectUrlDialog(url: string) {
      const dialogRef = this.dialog.open(EditValueDialogComponent, {
        data: {
          title: "Muuda väljalogimise tagasisuunasmispäringu URL-i",
          url: url
        },
        backdropClass: 'cdk-overlay-transparent-backdrop',
        hasBackdrop: true,
      });

      dialogRef.afterClosed().subscribe(({newUrl, oldUrl}) => {
        if (newUrl !== "" && newUrl !== undefined && oldUrl !== "" && oldUrl !== undefined) {
          const index = this.newData.post_logout_redirect_uris.indexOf(oldUrl);
          this.newData.post_logout_redirect_uris[index] = newUrl;
        }
      });
    }
}
