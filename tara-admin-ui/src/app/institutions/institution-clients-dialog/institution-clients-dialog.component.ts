import {Component, Inject, OnInit} from '@angular/core';
// @ts-ignore
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {Client} from "../../clients/model/client";

@Component({
  selector: 'app-institution-clients-dialog',
  templateUrl: './institution-clients-dialog.component.html',
  styleUrls: ['./institution-clients-dialog.component.css']
})
export class InstitutionClientsDialogComponent implements OnInit {
  constructor(@Inject(MAT_DIALOG_DATA) public data: () => Promise<Client[]>,
              public dialog: MatDialog) {
  }

  ngOnInit(): void {
  }

  close() {
    this.dialog.closeAll();
  }
}
