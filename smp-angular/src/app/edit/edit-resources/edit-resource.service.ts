import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {HttpClient, HttpParams} from '@angular/common/http';
import {SecurityService} from "../../security/security.service";
import {User} from "../../security/user.model";
import {SmpConstants} from "../../smp.constants";
import {GroupRo} from "../../common/model/group-ro.model";
import {ResourceRo} from "../../common/model/resource-ro.model";
import {TableResult} from "../../common/model/table-result.model";
import {DomainRo} from "../../common/model/domain-ro.model";
import {DocumentRo} from "../../common/model/document-ro.model";
import {SubresourceRo} from "../../common/model/subresource-ro.model";
import {
  ReviewDocumentVersionRo
} from "../../common/model/review-document-version-ro.model";

/**
 * The EditResourceService is used for server interaction on resources, sub-resources and it's documents.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Injectable()
export class EditResourceService {

  selectedResource: ResourceRo;
  selectedSubresource: SubresourceRo;
  selectedReviewDocument: ReviewDocumentVersionRo;

  constructor(
    private http: HttpClient,
    private securityService: SecurityService) {
  }

  /**
   * Method return observable of resource list from the server for resource-admin role for selected domain and group filter and paginating data.
   *
   * @param group  to which resource belongs to.
   * @param domain domain to which groupt and resource belongs to.
   * @param filter filter for resource list.
   * @param page paginating data - page number
   * @param pageSize paginating data - page size
   * @returns observable of TableResult<ResourceRo>
   */
  public getGroupResourcesForResourceAdminObservable(group: GroupRo, domain: DomainRo, filter: any, page: number, pageSize: number): Observable<TableResult<ResourceRo>> {
    return this.getGroupResourcesForUserTypeObservable('resource-admin', group, domain, filter, page, pageSize);
  }

  /**
   * Method allows group admin to update the resource properties
   * @param resource
   * @param group
   * @param domain
   */
  updateResourceForGroup(resource: ResourceRo, group: GroupRo, domain: DomainRo): Observable<ResourceRo> {
    const currentUser: User = this.securityService.getCurrentUser();

    return this.http.post<ResourceRo>(SmpConstants.REST_EDIT_RESOURCE_UPDATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain?.domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, group?.groupId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId), resource);
  }

  /**
   * Method return observable of resource list from the server for resource-admin role for selected domain and group filter and paginating data.
   *
   * @param userType user type for which resource list is returned.
   * @param group  to which resource belongs to.
   * @param domain domain to which groupt and resource belongs to.
   * @param filter filter for resource list.
   * @param page paginating data - page number
   * @param pageSize paginating data - page size
   * @returns observable of TableResult<ResourceRo>
   */
  public getGroupResourcesForUserTypeObservable(userType: string, group: GroupRo, domain: DomainRo, filter: any, page: number, pageSize: number): Observable<TableResult<ResourceRo>> {

    let params: HttpParams = new HttpParams()
      .set(SmpConstants.PATH_QUERY_FILTER_TYPE, userType)
      .set('page', !page ? "0" : page.toString())
      .set('pageSize', !pageSize ? "5" : pageSize.toString());

    if (!!filter) {
      for (let filterProperty in filter) {
        if (filter.hasOwnProperty(filterProperty)) {
          // must encode else problem with + sign
          params = params.set(filterProperty, encodeURIComponent(filter[filterProperty]));
        }
      }
    }

    let currentUser: User = this.securityService.getCurrentUser();
    return this.http.get<TableResult<ResourceRo>>(SmpConstants.REST_EDIT_RESOURCE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain?.domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, group?.groupId), {params});
  }

  /**
   * Method return observable of Document object from the server.
   * @param resource resource for which document is returned.
   * @param version version of document - if null current version is returned.
   * @returns observable of DocumentRo
   */
  public getResourceDocumentObservable(resource: ResourceRo, version: number = null): Observable<DocumentRo> {
    let params: HttpParams = null;
    if (version) {
      params = new HttpParams()
        .set('version', version);
    }
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.get<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_RESOURCE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId), {params});
  }

  /**
   * Method return observable of Document object for subresource from the server.
   * @param subresource subresource for which document is returned.
   * @param resource resource of the subresource.
   * @param version version of document - if null current version is returned.
   * @returns observable of DocumentRo
   */
  public getSubresourceDocumentObservable(subresource: SubresourceRo, resource: ResourceRo, version: number = null): Observable<DocumentRo> {
    let params: HttpParams = null;
    if (version) {
      params = new HttpParams()
        .set('version', version);
    }
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.get<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId)
      .replace(SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID, subresource?.subresourceId), {params});
  }

  /**
   * Method returns observable for saving the document for resource to the server.
   *
   * @param resource resource for which document belongs to.
   * @param document document to be saved.
   * @returns observable of DocumentRo
   */
  public saveResourceDocumentObservable(resource: ResourceRo, document: DocumentRo): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.put<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_RESOURCE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId), document);
  }

  /**
   * Method returns observable for saving the document for subresource to the server.
   *
   * @param subresource subresource for which document belongs to.
   * @param resource parent resource of the subresource.
   * @param document document to be saved.
   * @returns observable of DocumentRo
   */
  public saveSubresourceDocumentObservable(subresource: SubresourceRo, resource: ResourceRo, document: DocumentRo): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.put<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId)
      .replace(SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID, subresource?.subresourceId), document);
  }

  /**
   * Method returns observable for validating the document for resource on the server.
   * @param resource resource for which document belongs to.
   * @param document document to be validated.
   * @returns document DocumentRo to be validated.
   */
  public validateResourceDocumentObservable(resource: ResourceRo, document: DocumentRo): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_RESOURCE_VALIDATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId), document);
  }

  /**
   * Method returns observable for validating the document for subresource on the server.
   * @param subresource subresource for which document belongs to.
   * @param resource parent resource of the subresource.
   * @param document DocumentRo to be validated.
   */
  public validateSubresourceDocumentObservable(subresource: SubresourceRo, resource: ResourceRo, document: DocumentRo): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE_VALIDATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId)
      .replace(SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID, subresource?.subresourceId), document);
  }

  /**
   * Method returns observable for publishing the document for resource on the server.
   * @param resource resource for which document belongs to.
   * @param document document to be published.
   */
  public publishResourceDocumentObservable(resource: ResourceRo, document: DocumentRo): Observable<DocumentRo> {
    return this.resourceDocumentActionObservable(resource, document, SmpConstants.REST_EDIT_DOCUMENT_RESOURCE_PUBLISH);
  }

  /**
   * Method returns observable for publishing the document for subresource on the server.
   * @param subresource subresource for which document belongs to.
   * @param resource resource of the subresource.
   * @param document document to be published.
   */
  public publishSubresourceDocumentObservable(subresource: SubresourceRo, resource: ResourceRo, document: DocumentRo): Observable<DocumentRo> {
    return this.subresourceDocumentActionObservable(subresource, resource, document, SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE_PUBLISH);
  }

  /**
   * Method returns observable for review request of the document for resource on the server.
   * @param resource resource for which document belongs to.
   * @param document document to be reviewed.
   */
  public reviewRequestForResourceDocumentObservable(resource: ResourceRo, document: DocumentRo): Observable<DocumentRo> {
    return this.resourceDocumentActionObservable(resource, document, SmpConstants.REST_EDIT_DOCUMENT_RESOURCE_REVIEW_REQUEST);
  }

  /**
   * Method returns observable for review request of the document for subresource on the server.
   * @param subresource subresource for which document belongs to.
   * @param resource resource of the subresource.
   * @param document document to be reviewed.
   */
  public reviewRequestForSubresourceDocumentObservable(subresource: SubresourceRo, resource: ResourceRo, document: DocumentRo): Observable<DocumentRo> {
    return this.subresourceDocumentActionObservable(subresource, resource, document, SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE_REVIEW_REQUEST);
  }

  /**
   * Method returns observable for review approve of the document for resource on the server.
   * @param resource resource for which document belongs to.
   * @param document document to be approved.
   */
  public reviewApproveForResourceDocumentObservable(resource: ResourceRo, document: DocumentRo): Observable<DocumentRo> {
    return this.resourceDocumentActionObservable(resource, document, SmpConstants.REST_EDIT_DOCUMENT_RESOURCE_REVIEW_APPROVE);
  }

  /**
   * Method returns observable for review approve of the document for resource on the server.
   * @param subresource subresource for which document belongs to.
   * @param resource resource for which document belongs to.
   * @param document document to be approved.
   */
  public reviewApproveForSubresourceDocumentObservable(subresource: SubresourceRo, resource: ResourceRo, document: DocumentRo): Observable<DocumentRo> {
    return this.subresourceDocumentActionObservable(subresource, resource, document, SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE_REVIEW_APPROVE);
  }

  /**
   * Method returns observable for review reject of the document for resource on the server.
   * @param resource resource for which document belongs to.
   * @param document document to be rejected.
   */
  public reviewRejectResourceDocumentObservable(resource: ResourceRo, document: DocumentRo): Observable<DocumentRo> {
    return this.resourceDocumentActionObservable(resource, document, SmpConstants.REST_EDIT_DOCUMENT_RESOURCE_REVIEW_REJECT);
  }

  /**
   * Method returns observable for review reject of the document for resource on the server.
   * @param resource resource for which document belongs to.
   * @param document document to be rejected.
   */
  public reviewRejectSubresourceDocumentObservable(subresource: SubresourceRo, resource: ResourceRo, document: DocumentRo): Observable<DocumentRo> {
    return this.subresourceDocumentActionObservable(subresource, resource, document, SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE_REVIEW_REJECT);
  }

  /**
   * 'Method returns http-post observable for document requests for given url template address. The template should have properties
   * user-id and resource-id which are replaced with current user id and resource id. The document is sent as payload.
   * @param resource  resource for which document belongs to.
   * @param document document to be sent.
   * @param reviewUrlTemplate url template for document action.
   * @returns observable of DocumentRo
   */
  public resourceDocumentActionObservable(resource: ResourceRo, document: DocumentRo, reviewUrlTemplate: string): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<DocumentRo>(reviewUrlTemplate
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId), document);
  }

  /**
   * 'Method returns http-post observable for document requests for given url template address. The template should have properties
   * user-id, resource-id and subresource-id. which are replaced with current user id, resource id and subresource-id. The document is sent as payload.
   * @param subresource  subresource for which document belongs to.
   * @param resource  resource for which document belongs to.
   * @param document document to be sent.
   * @param reviewUrlTemplate url template for document action.
   * @returns observable of DocumentRo
   */
  public subresourceDocumentActionObservable(subresource: SubresourceRo, resource: ResourceRo, document: DocumentRo, reviewUrlTemplate: string): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<DocumentRo>(reviewUrlTemplate
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId)
      .replace(SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID, subresource?.subresourceId), document);
  }

  /**
   * 'Method returns http-post observable to generate of new payload for resource document.
   * @param resource  resource for which document belongs to.
   * @returns observable of DocumentRo
   */
  public generateResourceDocumentObservable(resource: ResourceRo): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_RESOURCE_GENERATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId), null);
  }

  /**
   * 'Method returns http-post observable to generate of new payload for subresource document.
   * @param subresource  subresource for which document belongs to.
   * @param resource  resource for which document belongs to.
   * @returns observable of DocumentRo
   */
  public generateSubresourceDocumentObservable(subresource: SubresourceRo, resource: ResourceRo): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE_GENERATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId)
      .replace(SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID, subresource?.subresourceId), null);
  }

  getSubResourcesForResource(resource: ResourceRo): Observable<SubresourceRo[]> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.get<SubresourceRo[]>(SmpConstants.REST_EDIT_SUBRESOURCE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId));
  }

  deleteSubresourceFromResource(subResource: SubresourceRo, resource: ResourceRo): Observable<SubresourceRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.delete<ResourceRo>(SmpConstants.REST_EDIT_SUBRESOURCE_DELETE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId)
      .replace(SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID, subResource?.subresourceId));
  }

  createSubResourceForResource(subresource: SubresourceRo, resource: ResourceRo): Observable<SubresourceRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.put<SubresourceRo>(SmpConstants.REST_EDIT_SUBRESOURCE_CREATE
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId),
      subresource);
  }
}
