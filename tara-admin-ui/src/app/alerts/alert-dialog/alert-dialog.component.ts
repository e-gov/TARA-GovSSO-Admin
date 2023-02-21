import { Component, Inject, OnInit } from '@angular/core';
// @ts-ignore
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from "@angular/material/dialog";
import { Alert, MessageTemplate } from "../model/alert";
import { environment } from "../../../environments/environment";
import { DateHelper } from "../../helper/datehelper";
import { MessageService } from "../../main/message/message.service";
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn } from "@angular/forms";
import { AuthService } from "../../auth/auth.service";
import { DateAdapter } from '@angular/material/core';
import { CustomDateAdapter } from './custom-date-adapter';

type DialogType = "ADD" | "UPDATE" | "INFO";
type TypeActionFn = (type: any, alert: Alert) => Promise<any>;

@Component({
  selector: 'app-alert-dialog',
  templateUrl: './alert-dialog.component.html',
  styleUrls: ['./alert-dialog.component.css'],
  providers: [
    {provide: DateAdapter, useClass: CustomDateAdapter}
  ]
})
export class AlertDialogComponent implements OnInit {

  readonly LANGUAGES = ['et', 'en', 'ru'];

  form: FormGroup;
  alertScopes: string[];

  dialogType: DialogType;
  obj?: Alert;
  onTypeAction: TypeActionFn;

  constructor(@Inject(MAT_DIALOG_DATA) data: {
                dialogType: DialogType,
                obj?: Alert,
                onTypeAction: TypeActionFn;
              },
              public mainDialog: MatDialogRef<AlertDialogComponent>,
              public messageService: MessageService,
              public dialog: MatDialog,
              public authService: AuthService) {
    this.alertScopes = environment.alertScopes;
    this.dialogType = data.dialogType;
    this.obj = data.obj;
    this.onTypeAction = data.onTypeAction;

    const currentDate = new Date();
    const defaultStartTime = new Date(currentDate.getUTCFullYear(), currentDate.getUTCMonth(), currentDate.getUTCDate(), 0, 0, 0)
    const defaultEndTime = new Date(currentDate.getUTCFullYear(), currentDate.getUTCMonth(), currentDate.getUTCDate() + 1, 0, 0, 0)
    this.form = new FormGroup({
      title: new FormControl(''),
      startDate: new FormControl(defaultStartTime),
      startTime: new FormControl('00:00:00'),
      endDate: new FormControl(defaultEndTime),
      endTime: new FormControl('00:00:00'),
      messageTemplates: new FormGroup(
        this.LANGUAGES.reduce((acc: any, language: string) => {
          return {...acc, [language]: new FormControl('')};
        }, {})
      ),
      alertScopes: new FormGroup(
        this.alertScopes.reduce((acc: any, alertScope: string) => {
          return {...acc, [alertScope]: new FormControl(false)};
        }, {}),
        this.alertScopesValidator())
    });
    if (this.obj != undefined) {
      const parsedStartDateTime = DateHelper.parseDateTime(this.obj.start_time)
      const parsedEndDateTime = DateHelper.parseDateTime(this.obj.end_time)
      this.form.setValue({
        title: this.obj.title,
        startDate: new Date(this.obj.start_time),
        startTime: parsedStartDateTime.time,
        endDate: new Date(this.obj.end_time),
        endTime: parsedEndDateTime.time,
        messageTemplates: this.LANGUAGES.reduce((acc: any, language: string) => {
          const template = this.obj!.login_alert.message_templates.find(template => template.locale === language);
          return {...acc, [language]: template != undefined ? template.message : ''};
        }, {}),
        alertScopes: this.alertScopes.reduce((acc: any, alertScope: string) => {
          return {...acc, [alertScope]: this.obj!.login_alert.auth_methods.includes(alertScope)};
        }, {})
      });
    }
  }

  ngOnInit(): void {
    this.mainDialog.backdropClick().subscribe(() => {
      this.mainDialog.close();
    });
  }

  isInfo(): boolean {
    return !['ADD', 'UPDATE'].includes(this.dialogType);
  }

  findExistingMessageTemplate(locale: string): MessageTemplate | undefined {
    return this.obj!.login_alert.message_templates.find(template => template.locale === locale);
  }

  save() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const formValue = this.form.value;
    const newObj: Alert = {
      id: this.obj?.id,
      title: formValue.title,
      login_alert: {
        enabled: this.obj?.login_alert.enabled !== false,
        auth_methods: Object.entries(formValue.alertScopes)
          .filter(([alertScope, enabled]) => enabled)
          .map(([alertScope, enabled]) => alertScope),
        message_templates: this.LANGUAGES
          .map(language => {
            return {locale: language, message: formValue.messageTemplates[language]};
          })
          .filter(template => template.message == null || template.message.trim().length > 0)
      },
      start_time: this.convertToDateTime(formValue.startDate, formValue.startTime),
      end_time: this.convertToDateTime(formValue.endDate, formValue.endTime),
    };
    this.onTypeAction(this.dialogType, newObj).then(() => {
      this.mainDialog.close();
    });
  }

  delete() {
    this.onTypeAction(this.dialogType, this.obj!).then(() => {
      this.mainDialog.close();
    });
  }

  convertToDateTime(dateValue: Date, timeString: string) {
    let time = timeString.split(":");

    let dateTime = new Date(Number(dateValue.getFullYear()), Number(dateValue.getMonth()), Number(dateValue.getDate()), Number(time[0]), Number(time[1]), Number(time[2]));

    return dateTime.toISOString();
  }

  getDisplayableDateTime(dateTime: string) {
    return DateHelper.convertToDisplayString(dateTime);
  }

  private alertScopesValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (this.authService.isSsoMode) {
        return null;
      }
      if(Object.values(control.value).some(checked => checked)) {
        return null;
      }
      return {
        alertScopeRequired: "Vähemalt 1 autentimisvahend peab olema valitud."
      };
    }
  }
}
