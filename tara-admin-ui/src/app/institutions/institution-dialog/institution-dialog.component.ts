import {Component, Inject, OnInit} from '@angular/core';
// @ts-ignore
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {Institution, InstitutionBillingSettings} from "../model/institution";
import {MessageService} from "../../main/message/message.service";
import {environment} from "../../../environments/environment";
import {FormControl} from "@angular/forms";
import {VerifyComponent} from "../../main/verify/verify.component";

@Component({
  selector: 'app-institution-dialog',
  templateUrl: './institution-dialog.component.html',
  styleUrls: ['./institution-dialog.component.css']
})
export class InstitutionDialogComponent implements OnInit {
  _name: string;
  _registry_code: string;
  _address: string;
  _email: string;
  _phone: string;
  _billing_settings: InstitutionBillingSettings;

  newData: Institution;
  _type = new FormControl();

  constructor(@Inject(MAT_DIALOG_DATA) public data: {
                dialogType: "ADD" | "UPDATE" | "INFO"
                obj: Institution,
                onTypeAction: (type: any, institution: Institution) => Promise<any>;
              },
              public mainDialog: MatDialogRef<InstitutionDialogComponent>,
              public messageService: MessageService,
              public dialog: MatDialog) {
    this.newData = JSON.parse(JSON.stringify(data.obj));

    this._name = this.newData.name!;
    this._registry_code = this.newData.registry_code!;
    this._type.setValue(this.newData.type.type);
    this._address = this.newData.address!;
    this._email = this.newData.email!;
    this._phone = this.newData.phone!;
    this._billing_settings = this.newData.billing_settings;
  }

  ngOnInit(): void {
    this.mainDialog.backdropClick().subscribe(() => {
      this.mainDialog.close();
    });
  }

  isInfo(): boolean {
    return !['ADD', 'UPDATE'].includes(this.data.dialogType);
  }

  save() {
    this.newData.name = this._name;
    this.newData.registry_code = this._registry_code;
    this.newData.type.type = this._type.value;
    this.newData.address = this._address;
    this.newData.email = this._email;
    this.newData.phone = this._phone;

    this.data.onTypeAction(this.data.dialogType, this.newData).then(() => {
      this.mainDialog.close();
    });
  }

  delete() {
    if (this.newData.client_ids.length > 0) {
      this.messageService.showMessage("Asutust ei saa kustutada, kui sellel on seotuid kliente", "ERROR", environment.errorMessageDurationInMills)
      return;
    }

    const dialogRef = this.dialog.open(VerifyComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      hasBackdrop: true,
      width: "400px",
      data: "Kas oled kindel, et soovid asutuse kustutada?"
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result === true) {
        this.data.onTypeAction(this.data.dialogType, this.data.obj).then(() => {
          this.mainDialog.close();
        });
      }
    });
  }

  getInstitutionTypes() {
    return Object.keys(environment.institutionType);
  }

  getInstitutionTypeValue(key: string) {
    // @ts-ignore
    return environment.institutionType[key];
  }
}
