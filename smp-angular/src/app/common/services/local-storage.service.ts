import {Injectable} from '@angular/core';
import {User} from "../../security/user.model";
import {ResourceRo} from "../model/resource-ro.model";
import {SubresourceRo} from "../model/subresource-ro.model";
import {
  ReviewDocumentVersionRo
} from "../model/review-document-version-ro.model";
import {NavigationNode} from "../../window/sidenav/navigation-model.service";

/**
 * Service to handle local storage operations
 *
 *
 * @since 5.1
 * @author Joze RIHTARSIC
 */
@Injectable()
export class LocalStorageService {

  private static readonly LOCAL_STORAGE_THEME_KEY: string = "smp-theme";
  private static readonly LOCAL_STORAGE_THEME_DEFAULT: string = "default_theme";

  private static readonly LOCAL_STORAGE_CURRENT_USER_KEY = 'currentUser';
  private static readonly LOCAL_STORAGE_EDIT_RESOURCE_SELECTED = 'selected-edit-resource';
  private static readonly LOCAL_STORAGE_EDIT_SUBRESOURCE_SELECTED = 'selected-edit-subresource';
  private static readonly LOCAL_STORAGE_EDIT_REVIEW_VERSION_SELECTED = 'selected-edit-review-version';
  private static readonly LOCAL_STORAGE_EDIT_DOCUMENT_VERSION_SELECTED = 'selected-edit-document-version';
  private static readonly LOCAL_STORAGE_NAVIGATION_PATH = 'navigation-path';


  constructor() {
  }

  private storeJSONEntity(entity: any, storageKey: string): void {
    if (!entity) {
      localStorage.removeItem(storageKey);
      return;
    }
    let entityAsString: string = JSON.stringify(entity);
    localStorage.setItem(storageKey, entityAsString);
  }

  private getJSONEntity(storageKey: string): any {
    let entityAsString: string = localStorage.getItem(storageKey);
    if (!entityAsString) {
      return null;
    }
    return JSON.parse(entityAsString);
  }

  private storeJSONArray(arr: any[], storageKey: string): void {
    if (!arr) {
      localStorage.removeItem(storageKey);
      return;
    }
    let entityAsString: string = JSON.stringify(arr);
    localStorage.setItem(storageKey, entityAsString);
  }

  private getJSONArray(storageKey: string): any[] {
    let arrayAsString: string = localStorage.getItem(storageKey);
    if (!arrayAsString) {
      return [];
    }
    return JSON.parse(arrayAsString);
  }


  /**
   * Method stores the theme to localStorage
   * @param theme
   */
  saveUserTheme(theme: string) {
    if (!!theme && theme != LocalStorageService.LOCAL_STORAGE_THEME_DEFAULT) {
      localStorage.setItem(LocalStorageService.LOCAL_STORAGE_THEME_KEY, theme);
    } else {
      localStorage.removeItem(LocalStorageService.LOCAL_STORAGE_THEME_KEY)
    }
  };

  /**
   * Get user theme from local storage
   */
  public getUserTheme(): string {
    return localStorage.getItem(LocalStorageService.LOCAL_STORAGE_THEME_KEY);
  }

  public storeUserDetails(user: User): void {
    this.storeJSONEntity(user, LocalStorageService.LOCAL_STORAGE_CURRENT_USER_KEY);
  }

  public getUserDetails(): User {
    return this.getJSONEntity(LocalStorageService.LOCAL_STORAGE_CURRENT_USER_KEY);
  }

  public storeSelectedResource(resource: ResourceRo): void {
    this.storeJSONEntity(resource, LocalStorageService.LOCAL_STORAGE_EDIT_RESOURCE_SELECTED);
  }

  public getSelectedResource(): ResourceRo {
    return this.getJSONEntity(LocalStorageService.LOCAL_STORAGE_EDIT_RESOURCE_SELECTED);
  }

  public storeSelectedSubresource(resource: SubresourceRo): void {
    this.storeJSONEntity(resource, LocalStorageService.LOCAL_STORAGE_EDIT_SUBRESOURCE_SELECTED);
  }

  public getSelectedSubresource(): SubresourceRo {
    return this.getJSONEntity(LocalStorageService.LOCAL_STORAGE_EDIT_SUBRESOURCE_SELECTED);
  }

  public storeSelectedReviewDocumentVersion(reviewVersion: ReviewDocumentVersionRo): void {
    this.storeJSONEntity(reviewVersion, LocalStorageService.LOCAL_STORAGE_EDIT_REVIEW_VERSION_SELECTED);
  }

  public getSelectedReviewDocumentVersion(): ReviewDocumentVersionRo {
    return this.getJSONEntity(LocalStorageService.LOCAL_STORAGE_EDIT_REVIEW_VERSION_SELECTED);
  }

  public storeSelectedDocumentVersionNumber(documentVersion: number): void {
    if (documentVersion == null) {
      localStorage.removeItem(LocalStorageService.LOCAL_STORAGE_EDIT_DOCUMENT_VERSION_SELECTED);
      return;
    }
    localStorage.setItem(LocalStorageService.LOCAL_STORAGE_EDIT_DOCUMENT_VERSION_SELECTED, documentVersion.toString());
  }

  public storeNavigationPath(navigationPath: NavigationNode[]): void {
    this.storeJSONArray(navigationPath, LocalStorageService.LOCAL_STORAGE_NAVIGATION_PATH);
  }

  public getNavigationPath(): NavigationNode[] {
    let path = this.getJSONArray(LocalStorageService.LOCAL_STORAGE_NAVIGATION_PATH);
    return path ? path : [];
  }

  public getSelectedDocumentVersionNumber(): number {
    let documentVersion: string = localStorage.getItem(LocalStorageService.LOCAL_STORAGE_EDIT_DOCUMENT_VERSION_SELECTED);
    if (documentVersion == null) {
      return null;
    }
    return parseInt(documentVersion);
  }

  /**
   *  Method clears all local storage except the theme. Theme is not
   *  cleared because it is used to set the theme on the next login.
   */
  public clearLocalStorage(): void {
    localStorage.removeItem(LocalStorageService.LOCAL_STORAGE_CURRENT_USER_KEY);
    localStorage.removeItem(LocalStorageService.LOCAL_STORAGE_EDIT_RESOURCE_SELECTED);
    localStorage.removeItem(LocalStorageService.LOCAL_STORAGE_EDIT_SUBRESOURCE_SELECTED);
    localStorage.removeItem(LocalStorageService.LOCAL_STORAGE_EDIT_REVIEW_VERSION_SELECTED);
    localStorage.removeItem(LocalStorageService.LOCAL_STORAGE_EDIT_DOCUMENT_VERSION_SELECTED);
    localStorage.removeItem(LocalStorageService.LOCAL_STORAGE_NAVIGATION_PATH);

  }

}
