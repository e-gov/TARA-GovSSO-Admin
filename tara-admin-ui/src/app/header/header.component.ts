import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Router} from "@angular/router";
import {AuthService} from "../auth/auth.service";
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class HeaderComponent implements OnInit {

  constructor(private router: Router,
              private authService: AuthService) {
    this.displayUser()
  }

  ngOnInit(): void {
  }

  logout() {
    return this.authService.logout()
      .subscribe(response => {
        this.router.navigateByUrl("/")
      })
  }

  applicationName() {
    if (this.authService.isSsoMode)
      return "GOVSSO haldusliides"
    else
      return "TARA haldusliides"
  }

  displayUser(): BehaviorSubject<string> {
    return this.authService.getCurrentUser()
  }
}
