import {Injectable} from "@angular/core";
import {map} from "rxjs/operators";
import {BehaviorSubject} from "rxjs";
import {RestClient} from "../http/client";
import {LoginRequest} from "./login-request.model";
import {Router} from "@angular/router";
import {Title} from "@angular/platform-browser";
import {MessageService} from "../main/message/message.service";

@Injectable()
export class AuthService {
  public hasCheckedUser: boolean = false;
  public isSsoMode: boolean = false;
  private readonly currentUser: BehaviorSubject<string>;

  constructor(
    private httpClient: RestClient,
    private router: Router,
    private titleService: Title,
    private messageService: MessageService,
  ) {
    // @ts-ignore
    this.currentUser = new BehaviorSubject<string>(null);
    this.checkCurrentUser();
    this.checkSsoMode();
  }

  getCurrentUser(): BehaviorSubject<string> {
    return this.currentUser;
  }

  checkCurrentUser(): any {
    return this.httpClient.get("/whoami")
      .toPromise()
      .then(response => {
        this.currentUser.next(response.username);
        this.hasCheckedUser = true;

        if (response.username) {
          this.messageService.clearMessage();
          this.router.navigate(["/main"])
        }
      }).catch(err => {
        this.hasCheckedUser = true;
      });
  }

  login(loginRequest: LoginRequest) {
    return this.httpClient.post("/login", loginRequest)
      .pipe(map(result => {
        this.currentUser.next(result.username);
        return result;
      }));
  }

  logout() {
    return this.httpClient.post("/logout", null)
      .pipe(map(result => {
        // @ts-ignore
        this.currentUser.next(null);
        return result;
      }));
  }

  checkSsoMode() {
    this.httpClient.get("/ssoMode")
      .toPromise()
      .then(response => {
        this.isSsoMode = response.ssoMode;
        if (this.isSsoMode)
          this.titleService.setTitle("GOVSSO haldusliides");
        else
          this.titleService.setTitle("TARA haldusliides");
      }).catch(err => {
      console.log("failed ssoMode check");
    });
  }
}
