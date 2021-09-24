import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'import-contacts',
  templateUrl: './import-clients-dialog.component.html',
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
export class ImportContactsDialogComponent implements OnInit {
  @ViewChild('input', {static: true}) input:any;
  fileToUpload: File = null;

  constructor(public dialog: MatDialogRef<ImportContactsDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: {}) {
    console.log('init');
  }

  ngOnInit(): void {
    this.input.nativeElement.focus();
    this.dialog.backdropClick().subscribe(() => {
      this.dialog.close();
    })
  }

  save() {
    if (this.fileToUpload !== null)
      this.dialog.close({fileToUpload: this.fileToUpload})
  }

  cancel() {
    this.dialog.close();
  }

  handleFileInput(e: Event) {
    this.fileToUpload = (e.target as HTMLInputElement).files[0];
  }
}
