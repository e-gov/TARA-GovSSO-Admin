import {Injectable} from "@angular/core";
import {RestClient} from "../http/client";
// @ts-ignore
import {Alert} from "./model/alert";
import {Observable} from "rxjs";

@Injectable()
export class AlertsService {

  constructor(private httpClient: RestClient) {
  }

  getAlerts(): Observable<Alert[]> {
    return this.httpClient.get("/alerts");
  }

  addAlert(alert: Alert): Observable<Alert[]> {
    return this.httpClient.post("/alerts", alert);
  }

  updateAlert(alert: Alert): Observable<Alert[]> {
    return this.httpClient.put("/alerts/" + alert.id, alert)
  }

  deleteAlert(alert: Alert): Observable<any> {
    return this.httpClient.delete("/alerts/" + alert.id)
  }
}
