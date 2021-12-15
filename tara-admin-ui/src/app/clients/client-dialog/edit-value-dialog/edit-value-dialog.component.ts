import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-add-value-dialog',
  templateUrl: './edit-value-dialog.component.html',
  styles: [`
    .btn-link {
      background: none !important;
      border: none;
      padding: 0 !important;
      font-family: arial, sans-serif;
      color: #069;
      text-decoration: underline;
      cursor: pointer;
      margin-left: 10px;
      float: right;
    }
  `]
})
export class EditValueDialogComponent implements OnInit {
  @ViewChild('input', {static: true}) input: any;
  oldUrl: string;

  constructor(public dialog: MatDialogRef<EditValueDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { title: string, url: string }) {
    this.oldUrl = data.url
  }

  ngOnInit(): void {
    this.dialog.backdropClick().subscribe(() => {
      this.dialog.close();
    })
  }

  save() {
    this.dialog.close({newUrl: this.data.url, oldUrl: this.oldUrl})
  }

  cancel() {
    this.dialog.close();
  }

}
