import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'tara-admin-ui';
  mainPage: string = "/login";

  ngOnInit(): void {
     this.mainPage = '/login';
  }
}
