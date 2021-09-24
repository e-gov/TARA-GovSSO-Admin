import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {MessageService} from "../main/message/message.service";

@Injectable()
export class RestClient {
  constructor(private http: HttpClient) {
  }

  get(url: string, params? : any): Observable<any> {
    return this.http.get(environment.backendUrl + url, { withCredentials: true, params: params })
  }

  post(url: string, body: any): Observable<any> {
    return this.http.post(environment.backendUrl + url, body, { withCredentials: true })
  }

  delete(url: string): Observable<any> {
    return this.http.delete(environment.backendUrl + url, { withCredentials: true })
  }

  put(url: string, body: any): Observable<any> {
    return this.http.put(environment.backendUrl + url, body, { withCredentials: true })
  }

  static handleGenericErrors = (messageService: MessageService, error: any) => {
    messageService.showMessage(JSON.stringify(error.error), "ERROR", environment.errorMessageDurationInMills);
    throw error;
  }
}
