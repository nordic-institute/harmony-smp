import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../../smp.constants";
import {User} from "../../security/user.model";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {SecurityService} from "../../security/security.service";
import {Observable, Subject} from "rxjs";
import {CredentialRo} from "../../security/credential.model";
import {AccessTokenRo} from "../../common/dialogs/access-token-generation-dialog/access-token-ro.model";

/**
 * Class handle current user settings such-as profile, credentials, DomiSMP settings... ,
 */
@Injectable()
export class UserService {

  private userProfileDataUpdateSubject = new Subject<User>();

  private userPwdCredentialsUpdateSubject = new Subject<CredentialRo>();

  private userAccessTokenCredentialsUpdateSubject = new Subject<CredentialRo[]>();
  private userAccessTokenCredentialUpdateSubject = new Subject<CredentialRo>();

  private userCertificateCredentialsUpdateSubject = new Subject<CredentialRo[]>();
  private userCertificateCredentialUpdateSubject = new Subject<CredentialRo>();


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
    this.http.get<CredentialRo>(SmpConstants.REST_PUBLIC_USER_CREDENTIAL_STATUS
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId))
      .subscribe((response: CredentialRo) => {
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
    this.http.get<CredentialRo[]>(SmpConstants.REST_PUBLIC_USER_ACCESS_TOKEN_CREDENTIALS
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId))
      .subscribe((response: CredentialRo[]) => {
        this.notifyAccessTokensUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  deleteUserAccessTokenCredential(credential: CredentialRo) {
    let user = this.getCurrentUser();
    if (!user || !credential) {
      return;
    }
    this.http.delete<CredentialRo>(SmpConstants.REST_PUBLIC_USER_MANAGE_ACCESS_TOKEN_CREDENTIAL
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, credential.credentialId))
      .subscribe((response: CredentialRo) => {
        this.notifyAccessTokenUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  updateUserAccessTokenCredential(credential: CredentialRo) {
    let user = this.getCurrentUser();
    if (!user || !credential) {
      return;
    }
    this.http.post<CredentialRo>(SmpConstants.REST_PUBLIC_USER_MANAGE_ACCESS_TOKEN_CREDENTIAL
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, credential.credentialId), credential)
      .subscribe((response: CredentialRo) => {
        this.notifyAccessTokenUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  updateUserCertificateCredential(credential: CredentialRo) {
    let user = this.getCurrentUser();
    if (!user || !credential) {
      return;
    }
    this.http.post<CredentialRo>(SmpConstants.REST_PUBLIC_USER_MANAGE_CERTIFICATE_CREDENTIAL
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, credential.credentialId), credential)
      .subscribe((response: CredentialRo) => {
        this.notifyCertificateUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  deleteUserCertificateCredential(credential: CredentialRo) {
    let user = this.getCurrentUser();
    if (!user || !credential) {
      return;
    }
    this.http.delete<CredentialRo>(SmpConstants.REST_PUBLIC_USER_MANAGE_CERTIFICATE_CREDENTIAL
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, credential.credentialId))
      .subscribe((response: CredentialRo) => {
        this.notifyCertificateUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  generateUserAccessTokenCredential(credential: CredentialRo) {
    let user = this.getCurrentUser();
    if (!user || !credential) {
      return;
    }
    return this.http.put<AccessTokenRo>(SmpConstants.REST_PUBLIC_USER_MANAGE_ACCESS_TOKEN_CREDENTIAL
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, 'create'),
      credential);
  }

  storeUserCertificateCredential(credential: CredentialRo) {
    let user = this.getCurrentUser();
    if (!user || !credential) {
      return;
    }
    this.http.put<CredentialRo>(SmpConstants.REST_PUBLIC_USER_MANAGE_CERTIFICATE_CREDENTIAL
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, credential.credentialId), credential)
      .subscribe((response: CredentialRo) => {
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
    this.http.get<CredentialRo[]>(SmpConstants.REST_PUBLIC_USER_CERTIFICATE_CREDENTIALS
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId))
      .subscribe((response: CredentialRo[]) => {
        this.notifyCertificatesUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  getUserCertificateCredentialObservable(credential: CredentialRo) {
    let user = this.getCurrentUser();
    if (!user) {
      return null;
    }
    return this.http.get<CredentialRo>(SmpConstants.REST_PUBLIC_USER_CERTIFICATE_CREDENTIAL
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID, credential.credentialId));
  }

  // notification event methods
  notifyProfileDataChanged(res: User) {
    this.userProfileDataUpdateSubject.next(res);
  }

  notifyPwdStatusUpdated(res: CredentialRo) {
    this.userPwdCredentialsUpdateSubject.next(res);
  }

  notifyAccessTokensUpdated(res: CredentialRo[]) {
    this.userAccessTokenCredentialsUpdateSubject.next(res);
  }

  notifyAccessTokenUpdated(res: CredentialRo) {
    this.userAccessTokenCredentialUpdateSubject.next(res);
  }

  notifyCertificatesUpdated(res: CredentialRo[]) {
    this.userCertificateCredentialsUpdateSubject.next(res);
  }

  notifyCertificateUpdated(res: CredentialRo) {
    this.userCertificateCredentialUpdateSubject.next(res);
  }

  // Observables for registering the observers
  onProfileDataChangedEvent(): Observable<any> {
    return this.userProfileDataUpdateSubject.asObservable();
  }

  onPwdCredentialsUpdateEvent(): Observable<CredentialRo> {
    return this.userPwdCredentialsUpdateSubject.asObservable();
  }


  onAccessTokenCredentialsUpdateSubject(): Observable<CredentialRo[]> {
    return this.userAccessTokenCredentialsUpdateSubject.asObservable();
  }

  onAccessTokenCredentialUpdateSubject(): Observable<CredentialRo> {
    return this.userAccessTokenCredentialUpdateSubject.asObservable();
  }

  onCertificateCredentiasUpdateSubject(): Observable<CredentialRo[]> {
    return this.userCertificateCredentialsUpdateSubject.asObservable();
  }

  onCertificateCredentialUpdateSubject(): Observable<CredentialRo> {
    return this.userCertificateCredentialUpdateSubject.asObservable();
  }

  getCurrentUser() {
    return this.securityService.getCurrentUser();
  }
}
