import {Injectable} from "@angular/core";
import {RestClient} from "../http/client";
// @ts-ignore
import {Client, InstitutionMetainfo} from "./model/client";
import {Observable} from "rxjs";

@Injectable()
export class ClientsService {

  constructor(private httpClient: RestClient) {}

  getInstitutionMetainfo(): Observable<InstitutionMetainfo[]> {
    return this.httpClient.get("/institutions/metainfo");
  }

  getClients(filterBy?: String): Observable<Client[]> {
    return this.httpClient.get("/clients", filterBy ? {"filter_by": filterBy} : undefined);
  }

  addClient(client: Client): Observable<Client[]> {
    return this.httpClient.post("/institutions/" + client.institution_metainfo.registry_code + "/clients", client);
  }

  updateClient(client: Client): Observable<Client[]> {
    return this.httpClient.put("/institutions/" + client.institution_metainfo.registry_code + "/clients/" + client.client_id, client)
  }

  deleteClient(client: Client): Observable<any> {
    return this.httpClient.delete("/institutions/" + client.institution_metainfo.registry_code + "/clients/" + client.client_id)
  }

  importClients(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);
    return this.httpClient.post('/clients/import', formData);
  }
}
