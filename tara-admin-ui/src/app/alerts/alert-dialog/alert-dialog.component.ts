import {Component, Inject, OnInit} from '@angular/core';
// @ts-ignore
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {Alert, LoginAlert} from "../model/alert";
import {environment} from "../../../environments/environment";
import {MatCheckboxChange} from "@angular/material/checkbox";
import {DateHelper} from "../../helper/datehelper";
import {MessageService} from "../../main/message/message.service";
import {NgForm} from "@angular/forms";
import {AuthService} from "../../auth/auth.service";

@Component({
  selector: 'app-alert-dialog',
  templateUrl: './alert-dialog.component.html',
  styleUrls: ['./alert-dialog.component.css']
})
export class AlertDialogComponent implements OnInit {
  _id: string;
  _title: string;
  _start_date: Date;
  _end_date: Date;
  _start_time: string;
  _end_time: string;
  _login_alert: LoginAlert;

  newData: Alert;

  constructor(@Inject(MAT_DIALOG_DATA) public data: {
                dialogType: "ADD" | "UPDATE" | "INFO"
                obj: Alert,
                onTypeAction: (type: any, alert: Alert) => Promise<any>;
              },
              public mainDialog: MatDialogRef<AlertDialogComponent>,
              public messageService: MessageService,
              public dialog: MatDialog,
              public authService: AuthService) {
    this.newData = JSON.parse(JSON.stringify(data.obj));

    let parsedStartDateTime = DateHelper.parseDateTime(this.newData.start_time)
    let parsedEndDateTime = DateHelper.parseDateTime(this.newData.end_time)

    this._id = this.newData.id!;
    this._title = this.newData.title;
    this._start_date = new Date(this.newData.start_time);
    this._end_date = new Date(this.newData.end_time);
    this._start_time = parsedStartDateTime.time;
    this._end_time = parsedEndDateTime.time;
    this._login_alert = JSON.parse(JSON.stringify(this.newData.login_alert));
  }

  ngOnInit(): void {
    this.mainDialog.backdropClick().subscribe(() => {
      this.mainDialog.close();
    });
  }

  isInfo(): boolean {
    return !['ADD', 'UPDATE'].includes(this.data.dialogType);
  }

  save(form: NgForm) {
    if (!this.authService.isSsoMode) {
      if (this._login_alert.auth_methods.length === 0) {
        document.getElementById("scope-alert").style.display = 'block';
        return;
      } else {
        document.getElementById("scope-alert").style.display = 'hidden';
      }
    }

    this.newData.id = this._id;
    this.newData.title = this._title;
    this.newData.login_alert = this._login_alert;
    this.newData.start_time = this.convertToDateTime(this._start_date, this._start_time);
    this.newData.end_time = this.convertToDateTime(this._end_date, this._end_time);
    this.newData.email_alert = undefined;

    this.data.onTypeAction(this.data.dialogType, this.newData).then(() => {
      this.mainDialog.close();
    });
  }

  delete() {
    this.data.onTypeAction(this.data.dialogType, this.data.obj).then(() => {
      this.mainDialog.close();
    });
  }

  getPossibleScopes() {
    return environment.alertScopes;
  }

  onScopeChange(event: MatCheckboxChange) {
    if (event.checked) {
      this._login_alert.auth_methods.push(event.source.name!)
    } else {
      this._login_alert.auth_methods =
        this._login_alert.auth_methods.filter((val: string) => val !== event.source.name)
    }
  }

  convertToDateTime(dateValue: Date, timeString: string) {
    let time = timeString.split(":");

    let dateTime = new Date(Number(dateValue.getFullYear()), Number(dateValue.getMonth()), Number(dateValue.getDate()), Number(time[0]), Number(time[1]), Number(time[2]));

    return dateTime.toISOString();
  }

  getDisplayableDateTime(dateTime: string) {
    return DateHelper.convertToDisplayString(dateTime);
  }
}
