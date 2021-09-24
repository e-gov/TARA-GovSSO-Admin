import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
// @ts-ignore
import {ClientNameTranslation} from "../../model/client";

@Component({
  selector: 'app-translation-dialog',
  templateUrl: './add-translation-dialog.component.html',
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
    select {
      background-color: white;
      color: black;
      border: 1px solid gray;
    }
  `]
})
export class AddTranslationDialogComponent implements OnInit {
  @ViewChild('input', {static: true}) input:any;
  _newData : ClientNameTranslation;

  constructor(public dialog: MatDialogRef<AddTranslationDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: ClientNameTranslation) {
    this._newData = JSON.parse(JSON.stringify(data));
  }

  ngOnInit(): void {
    this.input.nativeElement.focus();
    this.dialog.backdropClick().subscribe(() => {
      this.dialog.close();
    })
  }

  save() {
    this.dialog.close(this._newData)
  }

  cancel() {
    this.dialog.close();
  }

}
