import {Component, OnInit} from '@angular/core';
import {SmpConstants} from "../../smp.constants";
import {SecurityService} from "../../security/security.service";

@Component({
  selector: 'smp-review-tasks',
  templateUrl: './review-tasks.component.html',
  styleUrls: ['./review-tasks.component.css']
})
export class ReviewTasksComponent {

  constructor(private securityService: SecurityService,) {
  }

  get reviewTaskUrl(): string {
    return SmpConstants.REST_EDIT_REVIEW_TASK
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, this.securityService.getCurrentUser()?.userId);
  }

}
