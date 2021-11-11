import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {ClientDialogComponent} from "./client-dialog/client-dialog.component";
import {ImportContactsDialogComponent} from "./import-clients-dialog/import-clients-dialog.component";
// @ts-ignore
import {Client, InstitutionMetainfo} from './model/client';
import {ChangeSecretDialogComponent} from "./client-dialog/change-secret-dialog/change-secret-dialog.component";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatTableDataSource} from "@angular/material/table";
import {MessageService} from "../main/message/message.service";
import {ClientsService} from "./clients.service";
import {RestClient} from "../http/client";
import {DateHelper} from "../helper/datehelper";
import {environment} from "../../environments/environment";
import {AuthService} from "../auth/auth.service";

@Component({
  selector: 'app-clients',
  templateUrl: './clients.component.html',
  styleUrls: ['./clients.component.css']
})
export class ClientsComponent implements OnInit {
  @Input()
  dataSupplier?: () => Promise<Client[]>;
  @Input()
  withHeader: boolean = true;

  searchFilter?: string;
  waitingForApiResponse: boolean = false;
  displayedColumns: string[] = ['client_id', 'client_name', 'redirect_uris', 'scope', 'created_at', 'updated_at', 'action'];

  dataSource: MatTableDataSource<Client>;
  _searchField?: string;

  constructor(public dialog: MatDialog,
              public messageService: MessageService,
              public clientsService: ClientsService,
              public authService: AuthService) {
    if (this.authService.isSsoMode) {
      this.displayedColumns.forEach((element,index)=>{
         if(element=='scope') this.displayedColumns.splice(index,1);
      });
    }

    this.dataSource = new MatTableDataSource<Client>();
  }

  ngOnInit(): void {
    this.updateDatasource()
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

  openClientDialog(data: Client, dialogType: "ADD" | "UPDATE" | "INFO") {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      obj: data,
      institutions: this.getInstitutionMetainfo(),
      dialogType: dialogType,
      onTypeAction: (type: any, client: Client) => this.getTypeAction(type, client)
    };
    dialogConfig.width = "100%";
    dialogConfig.height = "100%";
    dialogConfig.backdropClass = 'cdk-overlay-transparent-backdrop';
    dialogConfig.hasBackdrop = true;

    this.dialog.open(ClientDialogComponent, dialogConfig);
  }

  openChangeSecretDialog(data: Client) {
    const dialogRef = this.dialog.open(ChangeSecretDialogComponent, {
        width: "600px",
        backdropClass: 'cdk-overlay-transparent-backdrop',
        hasBackdrop: true,
      }
    );

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result !== "" && result !== undefined) {
        data.client_secret_export_settings = result;
        this.updateClient(data, "Klientrakenduse parool on edukalt uuendatud.");
      }
    });
  }

  createNewClient() {
    let data: Client = {
      institution_metainfo: {
        name: undefined,
        registry_code: undefined,
        type: {type: "private"}
        },
      client_id: undefined,
      client_name: {et: undefined, en: undefined, ru: undefined},
      client_short_name: {et: undefined, en: undefined, ru: undefined},
      redirect_uris: [],
      post_logout_redirect_uris: [],
      scope: ["openid", "idcard", "mid", "smartid", "eidas", "eidasonly", "eidas:country:*", "email", "phone"],
      is_user_consent_required: false,
      client_url: undefined,
      backchannel_logout_uri: undefined,
      info_notification_emails: [],
      sla_notification_emails: [],
      created_at: undefined,
      updated_at: undefined,
      smartid_settings: {
        relying_party_name: undefined,
        relying_party_UUID: undefined,
        should_use_additional_verification_code_check: true
      },
      mid_settings: {
        relying_party_UUID: undefined,
        relying_party_name: undefined,
      },
      client_contacts: [],
      description: undefined,
      client_logo: undefined
    };

    this.openClientDialog(data, "ADD");
  }

  updateDatasource() {
    if (this.dataSupplier) {
      this.dataSupplier().then((clients: Client[]) => {
        this.dataSource = new MatTableDataSource<Client>(clients);
      })
    } else {
      this.getClients(this.searchFilter);
    }
  }

  getInstitutionMetainfo() {
    return this.clientsService.getInstitutionMetainfo();
  }

  getClients(filterBy?: string) {
    this.waitingForApiResponse = true;
    return this.clientsService.getClients(filterBy)
      .subscribe((clients: any) => {
        this.dataSource = new MatTableDataSource<Client>(clients);
        this.waitingForApiResponse = false;
      }, (error: any) => {
        this.waitingForApiResponse = false;
        this.messageService.showMessage(error.message, "ERROR", environment.errorMessageDurationInMills)
      });
  }

  addClient(client: Client) {
    this.waitingForApiResponse = true;
    return this.clientsService.addClient(client).toPromise()
      .then(() => {
        this.searchFilter = undefined;
        this.messageService.showMessage("Klientrakenduse edukalt lisatud.", "SUCCESS", environment.successMessageDurationInMills);
        this.updateDatasource();
        this.waitingForApiResponse = false;
      })
      .catch(error => {
        this.waitingForApiResponse = false;
        RestClient.handleGenericErrors(this.messageService, error)
      });
  }

  updateClient(client: Client, message?: string) {
    this.waitingForApiResponse = true;
    return this.clientsService.updateClient(client).toPromise()
      .then(() => {
        this.messageService.showMessage(message ? message : "Klientrakendus edukalt uuendatud.", "SUCCESS", environment.successMessageDurationInMills);
        this.updateDatasource();
        this.waitingForApiResponse = false;
      })
      .catch(error => {
        this.waitingForApiResponse = false;
        RestClient.handleGenericErrors(this.messageService, error)
      });
  }

  deleteClient(client: Client) {
    this.waitingForApiResponse = true;
    return this.clientsService.deleteClient(client).toPromise()
      .then(() => {
        this.messageService.showMessage("Klientrakenduse edukalt kustutatud.", "SUCCESS", environment.successMessageDurationInMills);
        this.updateDatasource();
        this.waitingForApiResponse = false;
      })
      .catch(error => {
        this.waitingForApiResponse = false;
        RestClient.handleGenericErrors(this.messageService, error);
      });
  }

  getTypeAction(dialogType: "ADD" | "UPDATE" | "INFO", client: Client) {
    if (dialogType === "ADD")
      return this.addClient(client);

    if (dialogType === "UPDATE")
      return this.updateClient(client);

    return this.deleteClient(client);
  }

  getDisplayableDateTime(dateTime: string) {
    return DateHelper.convertToDisplayString(dateTime);
  }

  search(filterBy: string) {
    this.searchFilter = filterBy;
    this.getClients(this.searchFilter)
  }

  importClients() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.autoFocus = true;
    dialogConfig.width = "100%";
    dialogConfig.height = "100%";
    dialogConfig.backdropClass = 'cdk-overlay-transparent-backdrop';
    dialogConfig.hasBackdrop = true;

    const dialogRef = this.dialog.open(ImportContactsDialogComponent, {
      width: "400px",
      backdropClass: 'cdk-overlay-transparent-backdrop',
      hasBackdrop: true,
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      window.scroll(0,0);
      if (result !== "" && result !== undefined) {
        this.waitingForApiResponse = true;
        this.clientsService.importClients(result.fileToUpload).toPromise()
          .then((response: any) => {
            if (response === "" || response === undefined) {
              this.messageService.showMessage( "Klientrakenduste import ebaõnnestus", "ERROR", environment.errorMessageDurationInMills);
            } else if (response.status === "FINISHED_SUCCESSFULLY") {
              this.messageService.showMessage( response.clients_import_success_count + " klientrakendust edukalt imporditud", "SUCCESS", environment.successMessageDurationInMills);
            } else {
              this.messageService.showMessage("Klientrakenduste importimine õnnestus osaliselt (kirjeid kokku: " + response.clients_count + ", imporditi: " + response.clients_import_success_count + "). " , "INFO", environment.infoMessageDurationInMills);
            }
            this.waitingForApiResponse = false;
          })
          .catch( error => {
            this.waitingForApiResponse = false;
            RestClient.handleGenericErrors(this.messageService, error);
          });
      }
    });
  }
}
