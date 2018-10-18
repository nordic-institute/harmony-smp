import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {CertificateRo} from './certificate-ro.model';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";

@Injectable()
export class CertificateService {

  constructor(private http: HttpClient) {}

  uploadCertificate$(payload): Observable<CertificateRo> {
    return this.http.post<CertificateRo>(SmpConstants.REST_CERTIFICATE, payload);
  }
}
