import {inject} from '@angular/core';
import {SecurityService} from '../security/security.service';
import {AlertMessageService} from "../common/alert-message/alert-message.service";
import {Authority} from "../security/authority.model";
import {ActivatedRouteSnapshot, CanActivateChildFn, CanActivateFn, RouterStateSnapshot} from "@angular/router";
import {EditResourceService} from "../edit/edit-resources/edit-resource.service";


export const activateChildResourceGuard: CanActivateFn =
  (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    console.log("Is user is authorized");
    const alertService: AlertMessageService = inject(AlertMessageService);
    const editResourceService: EditResourceService = inject(EditResourceService);

    let resourceUndefined:boolean = !editResourceService.selectedResource;
    if (resourceUndefined) {
      alertService.error('Resource/Subresource is not selected! Please select resource from resource edit panel.', true);
    }
    return !resourceUndefined;
  };
