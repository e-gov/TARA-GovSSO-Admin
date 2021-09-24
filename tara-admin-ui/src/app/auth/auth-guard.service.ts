import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import { Observable } from "rxjs";
import { AuthService } from "./auth.service";

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (!this.authService.hasCheckedUser) {
      return this.authService.checkCurrentUser()
        .then(() => !!this.authService.getCurrentUser().getValue());
    }

    if (this.authService.getCurrentUser().getValue()) {
      return true;
    }

    console.log("REDIRECTING BACK TO LOGIN");
    this.router.navigate(["/"]);
    return false;
  }

}
