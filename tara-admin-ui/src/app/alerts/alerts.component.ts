import {Component, OnInit, ViewChild} from '@angular/core';
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
// @ts-ignore
import {Alert, MessageTemplate} from './model/alert';
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatTableDataSource} from "@angular/material/table";
import {MessageService} from "../main/message/message.service";
import {AlertsService} from "./alerts.service";
import {AlertDialogComponent} from "./alert-dialog/alert-dialog.component";
import {RestClient} from "../http/client";
import {VerifyComponent} from "../main/verify/verify.component";
import {DateHelper} from "../helper/datehelper";
import {environment} from "../../environments/environment";
import {AuthService} from "../auth/auth.service";

@Component({
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
  styleUrls: ['./alerts.component.css']
})
export class AlertsComponent implements OnInit {
  displayedColumns: string[] = ['title', 'start_time', 'duration', 'display_for_methods', 'description', 'created_at', 'updated_at', 'action'];

  dataSource: MatTableDataSource<Alert>;

  constructor(public dialog: MatDialog,
              public messageService: MessageService,
              public alertsService: AlertsService,
              public authService: AuthService) {
    if (this.authService.isSsoMode) {
      this.displayedColumns.forEach((element,index)=>{
           if(element=='display_for_methods') this.displayedColumns.splice(index,1);
        });
    }

    this.dataSource = new MatTableDataSource<Alert>([])
  }

  ngOnInit(): void {
    this.getAlerts();
  }

  ngOnDestroy() {
    this.dialog.closeAll();
  }

  @ViewChild(MatPaginator, {static: false})
  set paginator(value: MatPaginator) {
    if (this.dataSource) {
      this.dataSource.paginator = value;
    }
  }

  @ViewChild(MatSort, {static: false})
  set sort(value: MatSort) {
    if (this.dataSource) {
      this.dataSource.sort = value;
    }
  }

  openAlertDialog(data: Alert, dialogType: "ADD" | "UPDATE" | "INFO") {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      obj: data,
      dialogType: dialogType,
      onTypeAction: (type: any, alert: Alert) => this.getTypeAction(type, alert)
    };
    dialogConfig.width = "100%";
    dialogConfig.height = "100%";
    dialogConfig.backdropClass = 'cdk-overlay-transparent-backdrop';
    dialogConfig.hasBackdrop = true;

    this.dialog.open(AlertDialogComponent, dialogConfig);
  }

  createNewAlert() {
    let currentDate = new Date();
    let data: Alert = {
      title: "",
      start_time: new Date(currentDate.getUTCFullYear(), currentDate.getUTCMonth(), currentDate.getUTCDate(), 0, 0, 0).toISOString(),
      end_time: new Date(currentDate.getUTCFullYear(), currentDate.getUTCMonth(), currentDate.getUTCDate() + 1, 0, 0, 0).toISOString(),
      login_alert: {
        enabled: true,
        message_templates: [],
        auth_methods: []
      },
      email_alert: {
        enabled: false,
        send_at: "",
        message_templates: []
      }
    };

    let loginMessageTemplate: MessageTemplate = {
      message: "",
      locale: "et"
    };

    // let emailMessageTemplate: MessageTemplate = {
    //   message: "",
    //   locale: "et"
    // };

    data.login_alert.message_templates.push(loginMessageTemplate)
    // data.email_alert.message_templates!.push(emailMessageTemplate)

    this.openAlertDialog(data, "ADD");
  }

  getAlerts() {
    return this.alertsService.getAlerts()
      .subscribe((data: any) => {
        this.dataSource = new MatTableDataSource<Alert>(data);
      }, (error: any) => {
        this.messageService.showMessage(error.message, "ERROR", environment.errorMessageDurationInMills)
      });
  }

  addAlert(alert: Alert) {
    return this.alertsService.addAlert(alert).toPromise()
      .then(() => {
        this.messageService.showMessage("Teavitus \"" + alert.title + "\" edukalt lisatud", "SUCCESS", environment.successMessageDurationInMills)
        this.getAlerts();
      })
      .catch(error => RestClient.handleGenericErrors(this.messageService, error));
  }

  updateAlert(alert: Alert) {
    return this.alertsService.updateAlert(alert).toPromise()
      .then(() => {
        this.messageService.showMessage("Teavitus \"" + alert.title + "\" edukalt uuendatud", "SUCCESS", environment.successMessageDurationInMills)
        this.getAlerts();
      })
      .catch(error => RestClient.handleGenericErrors(this.messageService, error));
  }

  deleteAlert(alert: Alert) {
    const dialogRef = this.dialog.open(VerifyComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      hasBackdrop: true,
      width: "400px",
      data: "Kas oled kindel, et tahad teavituse kustutada?"
    });

    return dialogRef.afterClosed().toPromise()
      // @ts-ignore
      .then((result: any) => {
        if (result === true) {
          return this.alertsService.deleteAlert(alert).toPromise()
            .then(() => {
              this.messageService.showMessage("Teavitus \"" + alert.title + "\" edukalt eemaldatud", "SUCCESS", environment.successMessageDurationInMills)
              this.getAlerts();
            })
            .catch(error => RestClient.handleGenericErrors(this.messageService, error));
        }
      })
  }

  getTypeAction(dialogType: "ADD" | "UPDATE" | "INFO", alert: Alert) {
    if (dialogType === "ADD")
      return this.addAlert(alert);

    if (dialogType === "UPDATE")
      return this.updateAlert(alert);

    return this.deleteAlert(alert);
  }

  getDisplayableDateTime(dateTime: string) {
    return DateHelper.convertToDisplayString(dateTime);
  }
}
