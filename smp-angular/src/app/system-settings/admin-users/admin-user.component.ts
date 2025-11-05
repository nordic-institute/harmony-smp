import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {ConfirmationDialogComponent} from "../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {MatDialog, MatDialogConfig, MatDialogRef} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {CancelDialogComponent} from "../../common/dialogs/cancel-dialog/cancel-dialog.component";
import {SearchUserRo} from "../../common/model/search-user-ro.model";
import {AdminUserService} from "./admin-user.service";
import {TableResult} from "../../common/model/table-result.model";
import {finalize} from "rxjs/operators";
import {SecurityService} from "../../security/security.service";
import {
  PasswordChangeDialogComponent
} from "../../common/dialogs/password-change-dialog/password-change-dialog.component";
import {ApplicationRoleEnum} from "../../common/enums/application-role.enum";
import {HttpErrorHandlerService} from "../../common/error/http-error-handler.service";
import {EntityStatus} from "../../common/enums/entity-status.enum";
import {firstValueFrom, lastValueFrom} from "rxjs";
import {UserRo} from "../../common/model/user-ro.model";
import {TranslateService} from "@ngx-translate/core";
import {MatTableDataSource} from "@angular/material/table";
import {SmpTableColDef} from "../../common/components/smp-table/smp-table-coldef.model";
import {PageEvent} from "@angular/material/paginator";
import {SmpTableComponent} from "../../common/components/smp-table/smp-table.component";


@Component({
  templateUrl: './admin-user.component.html',
  styleUrls: ['./admin-user.component.css'],
  standalone: false
})
export class AdminUserComponent implements AfterViewInit, BeforeLeaveGuard {
  displayedColumns: string[] = ['username', 'fullName'];
  dataSource: MatTableDataSource<SearchUserRo> = new MatTableDataSource();
  userData: SearchUserRo[];
  selected?: SearchUserRo;
  managedUserData?: UserRo;
  columns: SmpTableColDef[];

  filterValue: string;
  resultsLength: number = 0;
  pageSize: number = 10;
  pageIndex: number = 0;
  isLoadingResults: boolean = false;
  @ViewChild('smpTableComponent') smpTableComponent: SmpTableComponent;

  constructor(private adminUserService: AdminUserService,
              private httpErrorHandlerService: HttpErrorHandlerService,
              private securityService: SecurityService,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private translateService: TranslateService) {
    this.columns = [
      {
        columnDef: 'username',
        header: 'admin.user.label.username',
        tooltip: (row: SearchUserRo) => row.username,
        cell: (row: SearchUserRo) => row.username
      } as SmpTableColDef,
      {
        columnDef: 'fullName',
        header: 'admin.user.label.full.name',
        tooltip: (row: SearchUserRo) => row.username,
        cell: (row: SearchUserRo) => row.fullName
      } as SmpTableColDef,
    ];
  }

  ngAfterViewInit() {
    this.loadTableData();
  }

  applyUserFilter(filterValue: string) {
    this.filterValue= filterValue?.trim().toLowerCase();
    this.loadTableData();
  }

  onPageChanged(page: PageEvent) {
    this.pageIndex = page.pageIndex;
    this.pageSize = page.pageSize;
    this.loadTableData();
  }

  loadTableData(selectUsername: string = null) {
    this.isLoadingResults = true;

    this.adminUserService.getUsersObservable(this.filterValue, this.pageIndex, this.pageSize)
      .pipe(
        finalize(() => {
          this.isLoadingResults = false;
        }))
      .subscribe((result: TableResult<SearchUserRo>) => {
          this.userData = [...result.serviceEntities];
          this.dataSource.data = this.userData;
          this.resultsLength = result.count;
          this.pageIndex = result.page;
          this.pageSize = result.pageSize;
          this.isLoadingResults = false;

          if (selectUsername) {
            this.onUserSelected(this.userData.find(user => user.username === selectUsername));
          }
        }
      );
  }

  onCreateUserClicked() {
    this.selected = null;
    this.managedUserData = {
      active: true,
      username: "",
      role: ApplicationRoleEnum.USER
    }
  }

  onDiscardNew() {
    this.selected = null;
    this.managedUserData = null;
  }

  public onUserSelected(userSelected: SearchUserRo) {
    if (this.selected === userSelected) {
      return;
    }
    if (this.isDirty()) {
      firstValueFrom(this.dialog.open(CancelDialogComponent).afterClosed())
        .then((canChange: boolean) => {
          if (canChange) {
            this.selectAndRetrieveUserData(userSelected);
          }
        });
    } else {
      console.log("set selected 1 ");
      this.selectAndRetrieveUserData(userSelected);
    }
  }

  public selectAndRetrieveUserData(selectUser: SearchUserRo) {
    // clear old data
    if (!selectUser) {
      return;

    }
    this.adminUserService.getUserDataObservable(selectUser.userId).subscribe(
      {
        next: (user: UserRo) => {
          this.managedUserData = user;
          this.selected = selectUser;
        }, error: (error) => {
          this.managedUserData = null;
          if (this.httpErrorHandlerService.logoutOnInvalidSessionError(error)) {
            return;
          }
          this.alertService.error(error.error?.errorDescription)
        }
      });
  }

  onSaveUserEvent(user: UserRo) {
    if (!user.userId) {
      this.createUserData(user);
    } else {
      this.updateUserData(user);
    }
  }

  updateUserData(user: UserRo) {
    // capture this to variable because of async call 'this' inside targets the wrong object
    const thatAdminUserComponent = this;
    // change only allowed data
    this.adminUserService.updateManagedUser(user).subscribe({
      async next(user: UserRo) {
        if (user) {
          thatAdminUserComponent.selected = null;
          thatAdminUserComponent.managedUserData = null;
          thatAdminUserComponent.loadTableData(user.username);
          thatAdminUserComponent.alertService.success(await lastValueFrom(thatAdminUserComponent.translateService.get("admin.user.success.update", {username: user.username})));

        }
      }, error(error) {
        if (thatAdminUserComponent.httpErrorHandlerService.logoutOnInvalidSessionError(error)) {
          return;
        }
        thatAdminUserComponent.alertService.error(error.error?.errorDescription)
      }
    });
  }

  createUserData(user: UserRo) {
    // change only allowed data
    // capture this to variable because of async call 'this' inside targets the wrong object
    const thatAdminUserComponent = this;
    this.adminUserService.createManagedUser(user).subscribe({
      async next(user: UserRo) {
        if (user) {
          thatAdminUserComponent.selected = null;
          thatAdminUserComponent.managedUserData = null;
          thatAdminUserComponent.filterValue = user.username;
          thatAdminUserComponent.loadTableData(user.username);
          thatAdminUserComponent.alertService.success(await lastValueFrom(thatAdminUserComponent.translateService.get("admin.user.success.create", {username: user.username})));
        }
      }, error(error) {
        if (thatAdminUserComponent.httpErrorHandlerService.logoutOnInvalidSessionError(error)) {
          return;
        }
        thatAdminUserComponent.alertService.error(error.error?.errorDescription)
      }
    });
  }

  async onDeleteSelectedUserClicked() {
    // capture this to variable because of async call 'this' inside targets the wrong object
    const thatAdminUserComponent = this;
    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: await lastValueFrom(thatAdminUserComponent.translateService.get("admin.user.delete.confirmation.dialog.title", {username: this.managedUserData?.username})),
        description: await lastValueFrom(thatAdminUserComponent.translateService.get("admin.user.delete.confirmation.dialog.description"))
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteUser(thatAdminUserComponent.managedUserData);
      }
    });
  }

  deleteUser(user: UserRo) {
    // capture this to variable because of async call 'this' inside targets the wrong object
    const thatAdminUserComponent = this;
    // change only allowed data
    this.adminUserService.deleteManagedUser(user).subscribe({
      async next(user: UserRo) {
        if (user) {
          thatAdminUserComponent.selected = null;
          thatAdminUserComponent.managedUserData = null;
          thatAdminUserComponent.loadTableData();
          thatAdminUserComponent.alertService.success(await lastValueFrom(thatAdminUserComponent.translateService.get("admin.user.success.delete", {username: user.username})));
        }
      }, error(error) {
        if (thatAdminUserComponent.httpErrorHandlerService.logoutOnInvalidSessionError(error)) {
          return;
        }
        thatAdminUserComponent.alertService.error(error.error?.errorDescription)
      }
    });
  }

  changeUserPasswordEvent(user: UserRo) {
    const formRef: MatDialogRef<any> = this.changePasswordDialog({
      data: {
        user: user,
        adminUser: user.userId != this.securityService.getCurrentUser().userId
      },
    });
    formRef.afterClosed().subscribe(async result => {
      if (result) {
        this.selected = null;
        this.managedUserData = null;
        this.loadTableData(user.username);
        this.alertService.success(await lastValueFrom(this.translateService.get("admin.user.success.password.updated")));
      }
    });
  }

  public changePasswordDialog(config?: MatDialogConfig): MatDialogRef<PasswordChangeDialogComponent> {
    return this.dialog.open(PasswordChangeDialogComponent, this.convertConfig(config));
  }

  private convertConfig(config) {
    return (config?.data)
      ? {
        ...config,
        data: {
          ...config.data,
          mode: config.data.mode || (config.data.edit ? EntityStatus.PERSISTED : EntityStatus.NEW)
        }
      }
      : config;
  }

  isDirty(): boolean {
    return false;
  }

  isNew(): boolean {
    return !this.selected && !this.selected?.userId
  }

  get canNotDelete(): boolean {
    return !this.selected || this.isLoggedInUser
  }

  get editMode(): boolean {
    return this.isDirty();
  }

  get isLoggedInUser() {
    return this.securityService.getCurrentUser()?.userId == this.managedUserData?.userId
  }
}
