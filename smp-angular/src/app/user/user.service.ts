import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {User} from "../security/user.model";
import {AlertMessageService} from "../common/alert-message/alert-message.service";
import {SecurityService} from "../security/security.service";
import {Observable, Subject} from "rxjs";
import {Credential} from "../security/credential.model";
import {AccessTokenRo} from "../common/dialogs/access-token-generation-dialog/access-token-ro.model";

/**
 * Class handle current user settings such-as profile, credentials, DomiSMP settings... ,
 */
@Injectable()
export class UserService {

  private userProfileDataUpdateSubject = new Subject<User>();

  private userPwdCredentialsUpdateSubject = new Subject<Credential>();

  private userAccessTokenCredentialsUpdateSubject = new Subject<Credential[]>();
  private userAccessTokenCredentialUpdateSubject = new Subject<Credential>();

  private userCertificateCredentialsUpdateSubject = new Subject<Credential[]>();
  private userCertificateCredentialUpdateSubject = new Subject<Credential>();


  constructor(
    private http: HttpClient,
    private securityService: SecurityService,
    private alertService: AlertMessageService,
  ) {
  }

  updateUser(user: User) {
    this.http.put<User>(SmpConstants.REST_PUBLIC_USER_UPDATE.replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId), user).subscribe(response => {
      this.notifyProfileDataChanged(response)
      this.securityService.updateUserDetails(response);
      this.alertService.success('The operation \'update user\' completed successfully.');
    }, err => {
      this.alertService.exception('The operation \'update user\' not completed successfully.', err);
    });
  }


  getUserPwdCredentialStatus() {
    let user = this.securityService.getCurrentUser();
    if (user == null) {
      return;
    }
    this.http.get<Credential>(SmpConstants.REST_PUBLIC_USER_CREDENTIAL_STATUS
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId))
      .subscribe((response: Credential) => {
        this.notifyPwdStatusUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  getUserAccessTokenCredentials() {
    let user = this.getCurrentUser();
    if (!user) {
      return;
    }
    this.http.get<Credential[]>(SmpConstants.REST_PUBLIC_USER_ACCESS_TOKEN_CREDENTIALS
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId))
      .subscribe((response: Credential[]) => {
        this.notifyAccessTokensUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  deleteUserAccessTokenCredential(credential: Credential) {
    let user = this.getCurrentUser();
    if (!user || !credential) {
      return;
    }
    this.http.delete<Credential>(SmpConstants.REST_PUBLIC_USER_MANAGE_ACCESS_TOKEN_CREDENTIAL
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, credential.credentialId))
      .subscribe((response: Credential) => {
        this.notifyAccessTokenUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  updateUserAccessTokenCredential(credential: Credential) {
    let user = this.getCurrentUser();
    if (!user || !credential) {
      return;
    }
    this.http.post<Credential>(SmpConstants.REST_PUBLIC_USER_MANAGE_ACCESS_TOKEN_CREDENTIAL
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, credential.credentialId), credential)
      .subscribe((response: Credential) => {
        this.notifyAccessTokenUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  updateUserCertificateCredential(credential: Credential) {
    let user = this.getCurrentUser();
    if (!user || !credential) {
      return;
    }
    this.http.post<Credential>(SmpConstants.REST_PUBLIC_USER_MANAGE_CERTIFICATE_CREDENTIAL
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, credential.credentialId), credential)
      .subscribe((response: Credential) => {
        this.notifyCertificateUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  deleteUserCertificateCredential(credential: Credential) {
    let user = this.getCurrentUser();
    if (!user || !credential) {
      return;
    }
    this.http.delete<Credential>(SmpConstants.REST_PUBLIC_USER_MANAGE_CERTIFICATE_CREDENTIAL
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, credential.credentialId))
      .subscribe((response: Credential) => {
        this.notifyCertificateUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  generateUserAccessTokenCredential(credential: Credential) {
    let user = this.getCurrentUser();
    if (!user || !credential) {
      return;
    }
    return this.http.put<AccessTokenRo>(SmpConstants.REST_PUBLIC_USER_MANAGE_ACCESS_TOKEN_CREDENTIAL
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, 'create'),
      credential);
  }

  storeUserCertificateCredential(credential: Credential) {
    let user = this.getCurrentUser();
    if (!user || !credential) {
      return;
    }
    this.http.put<Credential>(SmpConstants.REST_PUBLIC_USER_MANAGE_CERTIFICATE_CREDENTIAL
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, credential.credentialId), credential)
      .subscribe((response: Credential) => {
        this.notifyCertificateUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }


  // -------------------------------------------------------------------
  // certificates
  getUserCertificateCredentials() {
    let user = this.getCurrentUser();
    if (!user) {
      return;
    }
    this.http.get<Credential[]>(SmpConstants.REST_PUBLIC_USER_CERTIFICATE_CREDENTIALS
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId))
      .subscribe((response: Credential[]) => {
        this.notifyCertificatesUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  getUserCertificateCredentialObservable(credential: Credential) {
    let user = this.getCurrentUser();
    if (!user) {
      return null;
    }
    return this.http.get<Credential>(SmpConstants.REST_PUBLIC_USER_CERTIFICATE_CREDENTIAL
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, credential.credentialId));
  }

  // notification event methods
  notifyProfileDataChanged(res: User) {
    this.userProfileDataUpdateSubject.next(res);
  }

  notifyPwdStatusUpdated(res: Credential) {
    this.userPwdCredentialsUpdateSubject.next(res);
  }

  notifyAccessTokensUpdated(res: Credential[]) {
    this.userAccessTokenCredentialsUpdateSubject.next(res);
  }

  notifyAccessTokenUpdated(res: Credential) {
    this.userAccessTokenCredentialUpdateSubject.next(res);
  }

  notifyCertificatesUpdated(res: Credential[]) {
    this.userCertificateCredentialsUpdateSubject.next(res);
  }

  notifyCertificateUpdated(res: Credential) {
    this.userCertificateCredentialUpdateSubject.next(res);
  }

  // Observables for registering the observers
  onProfileDataChangedEvent(): Observable<any> {
    return this.userProfileDataUpdateSubject.asObservable();
  }

  onPwdCredentialsUpdateEvent(): Observable<Credential> {
    return this.userPwdCredentialsUpdateSubject.asObservable();
  }


  onAccessTokenCredentialsUpdateSubject(): Observable<Credential[]> {
    return this.userAccessTokenCredentialsUpdateSubject.asObservable();
  }

  onAccessTokenCredentialUpdateSubject(): Observable<Credential> {
    return this.userAccessTokenCredentialUpdateSubject.asObservable();
  }

  onCertificateCredentiasUpdateSubject(): Observable<Credential[]> {
    return this.userCertificateCredentialsUpdateSubject.asObservable();
  }

  onCertificateCredentialUpdateSubject(): Observable<Credential> {
    return this.userCertificateCredentialUpdateSubject.asObservable();
  }

  getCurrentUser() {
    return this.securityService.getCurrentUser();
  }
}
