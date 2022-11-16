import { Component, Input } from '@angular/core';
// @ts-ignore
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from "@angular/material/dialog";
import { AbstractControl } from "@angular/forms";


@Component({
  selector: 'app-form-errors',
  templateUrl: './form-errors.component.html'
})
export class FormErrorsComponent {

  @Input() control: AbstractControl;

  constructor() {}

}
