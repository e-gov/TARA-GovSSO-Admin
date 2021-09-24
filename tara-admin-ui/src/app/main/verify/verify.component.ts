import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-verify',
  templateUrl: './verify.component.html'
})
export class VerifyComponent implements OnInit {
  _title: string;
  constructor(public dialog: MatDialogRef<VerifyComponent>, @Inject(MAT_DIALOG_DATA) public data: string) {
    this._title = data;
  }

  ngOnInit(): void {}

  continue() {
    this.dialog.close(true)
  }

  cancel() {
    this.dialog.close(false)
  }

}
