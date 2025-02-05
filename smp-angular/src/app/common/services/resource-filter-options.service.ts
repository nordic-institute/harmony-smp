import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {SmpConstants} from "../../smp.constants";
import {ResourceFilterOptionsRo} from "../model/resource-filter-options-ro.model";

@Injectable()
export class ResourceFilterOptionsService {

  constructor(private http: HttpClient) { }

  getResourceFilterOptions$(): Observable<ResourceFilterOptionsRo> {
    return this.http.get<ResourceFilterOptionsRo>(SmpConstants.REST_PUBLIC_SEARCH_RESOURCE_METADATA);
  }
}
