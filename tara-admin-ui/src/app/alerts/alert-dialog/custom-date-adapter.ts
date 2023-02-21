import { NativeDateAdapter, DateAdapter } from '@angular/material/core';

export class CustomDateAdapter extends NativeDateAdapter {
  parse(value: string) {
    let it=value.split('/');
    return new Date(+it[2],+it[1]-1,+it[0],12)
  }

  format(date: Date, displayFormat: Object) {
    return ('0'+date.getDate()).slice(-2)+'/'+('0'+(date.getMonth()+1)).slice(-2)+'/'+date.getFullYear()
  }
}
