import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {MessageService} from "../../../main/message/message.service";
import {environment} from "../../../../environments/environment";
import {ClientSecretExportSettings} from "../../model/client";


@Component({
  selector: 'change-secret-dialog',
  templateUrl: './change-secret-dialog.component.html',
  styles: [`
    .btn-link {
      background: none!important;
      border: none;
      padding: 0!important;
      /*optional*/
      font-family: arial, sans-serif;
      /*input has OS specific font-family*/
      color: #069;
      text-decoration: underline;
      cursor: pointer;
      margin-left: 10px;
      float: right;
    }
  `]
})
export class ChangeSecretDialogComponent implements OnInit {
  @ViewChild('input', {static: true}) input:any;

  _recipientIdCode?: string;
  _recipientEmail?: string;

  constructor(public dialog: MatDialogRef<ChangeSecretDialogComponent>,
              public messageService: MessageService,
              @Inject(MAT_DIALOG_DATA) public data: ClientSecretExportSettings) {
    if (data) {
      this._recipientEmail = data.recipient_email;
      this._recipientIdCode = data.recipient_id_code;
    }
  }

  ngOnInit(): void {
    this.input.nativeElement.focus();
    this.dialog.backdropClick().subscribe(() => {
      this.dialog.close();
    })
  }

  save() {
    if (!this.validate())
      this.messageService.showMessage("Saladuse väljastamiseks peavad olema esitatud nii isikukood (CDOC faili adressaat) kui ka e-mail.", "ERROR", environment.errorMessageDurationInMills);
    else {
      this.dialog.close({recipient_email: this._recipientEmail, recipient_id_code: this._recipientIdCode})
    }
  }

  cancel() {
    this.dialog.close();
  }

  validate(): boolean {
    if (!this._recipientEmail || !this._recipientIdCode) return false;
    // perform more checks

    return true;
  };

}
