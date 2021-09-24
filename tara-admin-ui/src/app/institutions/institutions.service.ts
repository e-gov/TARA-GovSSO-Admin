import {Injectable} from "@angular/core";
import {RestClient} from "../http/client";
// @ts-ignore
import {Institution} from "./model/institution";
import {Observable} from "rxjs";
import {Client} from "../clients/model/client";

@Injectable()
export class InstitutionsService {

  constructor(private httpClient: RestClient) {}

  getInstitutions(filterBy?: String): Observable<Institution[]> {
    return this.httpClient.get("/institutions", filterBy ? {"filter_by": filterBy} : undefined);
  }

  addInstitution(institution: Institution): Observable<Institution[]> {
    return this.httpClient.post("/institutions", institution);
  }

  updateInstitution(institution: Institution): Observable<Institution[]> {
    return this.httpClient.put("/institutions/" + institution.registry_code, institution)
  }

  deleteInstitution(institution: Institution): Observable<any> {
    return this.httpClient.delete("/institutions/" + institution.registry_code)
  }

  getClientsForInstitution(institution: Institution): Observable<Client[]> {
    return this.httpClient.get("/institutions/" + institution.registry_code + "/clients");
  }
}
