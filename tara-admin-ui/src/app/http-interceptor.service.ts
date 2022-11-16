import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError} from "rxjs/operators";
import {Injectable} from "@angular/core";
import {Router} from "@angular/router";
import {MessageService} from "./main/message/message.service";
import {environment} from "../environments/environment";

@Injectable()
export class HttpInterceptorService implements HttpInterceptor {

  constructor(private router: Router,
              public messageService: MessageService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse) {
          if (this.isAuthError(error)) {
            this.handleHttpErrorResponse(error);
            return []; // TODO: Throw error? Right now misleading success messages are shown when session has expired.
          } else {
            return throwError(error);
          }
        }

        return throwError(error);
      }));
  }

  private handleHttpErrorResponse(error: HttpErrorResponse) {
    const url = error.url!;
    if (url.includes("/login")) {
      this.messageService.showMessage(JSON.stringify(error.error), "ERROR", environment.errorMessageDurationInMills)
      return;
    }
    if (!url.includes("/whoami") && !url.includes("/ssoMode")) {
      this.messageService.showMessage("Session expired.", "ERROR", environment.errorMessageDurationInMills)
    }
    this.router.navigate(["/"]);
  }

  private isAuthError(error: HttpErrorResponse): boolean {
    return error.status === 401 || error.status === 403;
  }

}
