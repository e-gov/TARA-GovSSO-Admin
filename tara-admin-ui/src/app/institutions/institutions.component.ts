import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
// @ts-ignore
import {Institution} from './model/institution';
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatTableDataSource} from "@angular/material/table";
import {MessageService} from "../main/message/message.service";
import {InstitutionsService} from "./institutions.service";
import {InstitutionDialogComponent} from "./institution-dialog/institution-dialog.component";
import {RestClient} from "../http/client";
import {DateHelper} from "../helper/datehelper";
import {environment} from "../../environments/environment";
import {InstitutionClientsDialogComponent} from "./institution-clients-dialog/institution-clients-dialog.component";
import {Client} from "../clients/model/client";

@Component({
  selector: 'app-institutions',
  templateUrl: './institutions.component.html',
  styleUrls: ['./institutions.component.css']
})
export class InstitutionsComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['name', 'registry_code', 'client_ids', 'created_at', 'updated_at', 'action'];

  dataSource: MatTableDataSource<Institution>;
  _searchField?: string;

  constructor(public dialog: MatDialog,
              public messageService: MessageService,
              public institutionsService: InstitutionsService) {
    this.dataSource = new MatTableDataSource<Institution>([]);
  }

  ngOnInit(): void {
    this.getInstitutions();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.sort.sortChange.subscribe(() => {
      this.dataSource.sort = this.sort;
    });
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

  @ViewChild(MatSort, {static: false}) sort: MatSort;

  openInstitutionDialog(data: Institution, dialogType: "ADD" | "UPDATE" | "INFO") {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      obj: data,
      dialogType: dialogType,
      onTypeAction: (type: any, institution: Institution) => this.getTypeAction(type, institution)
    };
    dialogConfig.width = "100%";
    dialogConfig.height = "100%";
    dialogConfig.backdropClass = 'cdk-overlay-transparent-backdrop';
    dialogConfig.hasBackdrop = true;

    this.dialog.open(InstitutionDialogComponent, dialogConfig);
  }

  createNewInstitution() {
    let data: Institution = {
      registry_code: undefined,
      name: undefined,
      type: {type: "public"},
      client_ids: [],
      address: undefined,
      phone: undefined,
      email: undefined,
      billing_settings: {
        email: undefined
      },
    };

    this.openInstitutionDialog(data, "ADD");
  }

  getInstitutions(filterBy?: string) {
    this.institutionsService.getInstitutions(filterBy)
      .subscribe((data: any) => {
        this.dataSource = new MatTableDataSource<Institution>(data);
      }, (error: any) => {
        this.messageService.showMessage(error.message, "ERROR", environment.errorMessageDurationInMills)
      });
  }

  addInstitution(institution: Institution) {
    if (this.similarInstitutionAlreadyExists(institution)) {
      this.messageService.showMessage("Sama registrikoodiga asutus juba eksisteerib,", "ERROR", environment.successMessageDurationInMills);
      return Promise.reject();
    }

    return this.institutionsService.addInstitution(institution).toPromise()
      .then(() => {
        this._searchField = undefined;
        this.messageService.showMessage("Asutus " + institution.name + " edukalt lisatud", "SUCCESS", environment.successMessageDurationInMills);
        this.getInstitutions();
      })
      .catch(error => RestClient.handleGenericErrors(this.messageService, error));
  }

  updateInstitution(institution: Institution) {
    if (this.similarInstitutionAlreadyExists(institution)) {
      this.messageService.showMessage("Sama registrikoodiga asutus juba eksisteerib,", "ERROR", environment.successMessageDurationInMills);
      return Promise.reject();
    }

    return this.institutionsService.updateInstitution(institution).toPromise()
      .then(() => {
        this.messageService.showMessage("Asutus " + institution.name + " edukalt uuendatud", "SUCCESS", environment.successMessageDurationInMills);
        this.getInstitutions(this._searchField);
      })
      .catch(error => RestClient.handleGenericErrors(this.messageService, error));
  }

  deleteInstitution(institution: Institution) {
    return this.institutionsService.deleteInstitution(institution).toPromise()
      .then(() => {
        this.messageService.showMessage("Asutus " + institution.name + " edukalt eemaldatud", "SUCCESS", environment.successMessageDurationInMills);
        this.getInstitutions(this._searchField);
      })
      .catch(error => RestClient.handleGenericErrors(this.messageService, error));
  }

  getTypeAction(dialogType: "ADD" | "UPDATE" | "INFO", institution: Institution) {
    if (dialogType === "ADD")
      return this.addInstitution(institution);

    if (dialogType === "UPDATE")
      return this.updateInstitution(institution);

    return this.deleteInstitution(institution);
  }

  getDisplayableDateTime(dateTime: string) {
    return DateHelper.convertToDisplayString(dateTime);
  }

  similarInstitutionAlreadyExists(institution: Institution) {
    return this.dataSource.data.filter(obj => {
      return obj.registry_code == institution.registry_code && obj.id != institution.id;
    }).length > 0;
  }

  search() {
    this.getInstitutions(this._searchField)
  }

  openClientsDialog(institution: Institution) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.autoFocus = true;
    dialogConfig.width = "100%";
    dialogConfig.height = "100%";
    dialogConfig.backdropClass = 'cdk-overlay-transparent-backdrop';
    dialogConfig.hasBackdrop = true;
    dialogConfig.data = () => this.institutionsService.getClientsForInstitution(institution).toPromise();

    this.dialog.open(InstitutionClientsDialogComponent, dialogConfig);

  }
}
