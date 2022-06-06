import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-edit-value-dialog',
  templateUrl: './edit-value-dialog.component.html',
  styles: [`
    .btn-link {
      background: none !important;
      border: none;
      padding: 0 !important;
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
export class EditValueDialogComponent implements OnInit {
  @ViewChild('input', {static: true}) input: any;
  oldValue: string;

  constructor(public dialog: MatDialogRef<EditValueDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { title: string, value: string }) {
    this.oldValue = data.value
  }

  ngOnInit(): void {
    this.input.nativeElement.focus();
    this.dialog.backdropClick().subscribe(() => {
      this.dialog.close();
    })
  }

  save() {
    this.dialog.close({newValue: this.data.value, oldValue: this.oldValue})
  }

  cancel() {
    this.dialog.close();
  }

}
