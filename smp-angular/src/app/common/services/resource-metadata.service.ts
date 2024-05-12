import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {SmpConstants} from "../../smp.constants";
import {ResourceMetadataRo} from "../model/resource-metadata-ro.model";

@Injectable()
export class ResourceMetadataService {

  constructor(private http: HttpClient) { }

  getResourceMetadata$(): Observable<ResourceMetadataRo> {
    return this.http.get<ResourceMetadataRo>(SmpConstants.REST_PUBLIC_SEARCH_RESOURCE_METADATA);
  }
}
