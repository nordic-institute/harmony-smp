import {inject} from '@angular/core';
import {
  AlertMessageService
} from "../common/alert-message/alert-message.service";
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  RouterStateSnapshot
} from "@angular/router";
import {
  EditResourceService
} from "../edit/edit-resources/edit-resource.service";


export const activateChildReviewGuard: CanActivateFn =
  (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    const alertService: AlertMessageService = inject(AlertMessageService);
    const editResourceService: EditResourceService = inject(EditResourceService);

    return true;
  };
