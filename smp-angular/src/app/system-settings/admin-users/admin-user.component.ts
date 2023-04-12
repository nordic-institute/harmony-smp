import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {ConfirmationDialogComponent} from "../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {MatDialog, MatDialogConfig, MatDialogRef} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {CancelDialogComponent} from "../../common/dialogs/cancel-dialog/cancel-dialog.component";
import {SearchUserRo} from "../admin-domain/domain-member-panel/member-dialog/search-user-ro.model";
import {AdminUserService} from "./admin-user.service";
import {TableResult} from "../admin-domain/domain-member-panel/table-result.model";
import {finalize} from "rxjs/operators";
import {UserRo} from "../user/user-ro.model";
import {SecurityService} from "../../security/security.service";
import {
  PasswordChangeDialogComponent
} from "../../common/dialogs/password-change-dialog/password-change-dialog.component";
import {UserDetailsDialogMode} from "../user/user-details-dialog/user-details-dialog.component";
import {ApplicationRoleEnum} from "../../common/enums/application-role.enum";


@Component({
  moduleId: module.id,
  templateUrl: './admin-user.component.html',
  styleUrls: ['./admin-user.component.css']
})
export class AdminUserComponent implements AfterViewInit, BeforeLeaveGuard {
  displayedColumns: string[] = ['username', 'fullName'];

  selected?: SearchUserRo;

  managedUserData?: UserRo;

  userData: SearchUserRo[];
  filter: string;
  resultsLength: number = 0;
  isLoadingResults: boolean = false;


  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private adminUserService: AdminUserService,
              private securityService: SecurityService,
              private alertService: AlertMessageService,
              private dialog: MatDialog) {


  }

  ngAfterViewInit() {
    this.loadTableData();
  }

  onPageChanged(page: PageEvent) {
    this.loadTableData();
  }

  applyUserFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    if (this.filter === filterValue) {
      return;
    }
    this.filter = filterValue;
    this.loadTableData();
  }

  loadTableData() {

    this.isLoadingResults = true;

    this.adminUserService.getUsersObservable(this.filter, this.paginator.pageIndex, this.paginator.pageSize)
      .pipe(
        finalize(() => {
          this.isLoadingResults = false;
        }))
      .subscribe((result: TableResult<SearchUserRo>) => {
          this.userData = [...result.serviceEntities];
          this.resultsLength = result.count;
          this.isLoadingResults = false;
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

  public userSelected(userSelected: SearchUserRo) {
    if (this.selected === userSelected) {
      return;
    }
    if (this.isDirty()) {
      let canChangeTab = this.dialog.open(CancelDialogComponent).afterClosed().toPromise<boolean>();
      canChangeTab.then((canChange: boolean) => {
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
    this.managedUserData = null;
    if (!selectUser) {
      return;

    }
    this.adminUserService.getUserDataObservable(selectUser.userId).subscribe((user: UserRo) => {
      if (user) {
        this.managedUserData = user;
        this.selected = selectUser;
      }
    }, (error) => {
      this.alertService.error(error.error?.errorDescription)
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
    // change only allowed data
    this.adminUserService.updateManagedUser(user).subscribe(user => {
      if (user) {
        this.selected = null;
        this.managedUserData = null;
        this.loadTableData();
        this.alertService.success("User [" + user.username + "] updated!");
      }
    }, (error) => {
      this.alertService.error(error.error?.errorDescription)
    });
  }

  createUserData(user: UserRo) {
    // change only allowed data
    this.adminUserService.createManagedUser(user).subscribe(user => {
      if (user) {
        this.selected = null;
        this.managedUserData = null;
        this.loadTableData();
        this.alertService.success("User [" + user.username + "] created!");
      }
    }, (error) => {
      this.alertService.error(error.error?.errorDescription)
    });
  }

  onDeleteSelectedUserClicked() {

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Delete user " + this.managedUserData?.username + " from DomiSMP",
        description: "Action will permanently delete user! Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteUser(this.managedUserData);
      }
    });
  }

  deleteUser(user: UserRo) {

    // change only allowed data
    this.adminUserService.deleteManagedUser(user).subscribe(user => {
      if (user) {
        this.selected = null;
        this.managedUserData = null;
        this.loadTableData();
        this.alertService.success("User [" + user.username + "] deleted!");
      }
    }, (error) => {
      this.alertService.error(error.error?.errorDescription)
    });

  }

  changeUserPasswordEvent(user: UserRo) {
    const formRef: MatDialogRef<any> = this.changePasswordDialog({
      data: {
        user: user,
        adminUser: user.userId != this.securityService.getCurrentUser().userId
      },
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        //this.currentUserData.passwordExpireOn = result.passwordExpireOn;
        //this.currentUserData = {...this.currentUserData}
      }
    });
  }

  public changePasswordDialog(config?: MatDialogConfig): MatDialogRef<PasswordChangeDialogComponent> {
    return this.dialog.open(PasswordChangeDialogComponent, this.convertConfig(config));
  }


  private convertConfig(config) {
    return (config && config.data)
      ? {
        ...config,
        data: {
          ...config.data,
          mode: config.data.mode || (config.data.edit ? UserDetailsDialogMode.EDIT_MODE : UserDetailsDialogMode.NEW_MODE)
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
