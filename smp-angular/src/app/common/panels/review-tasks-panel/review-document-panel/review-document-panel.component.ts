import {Component, ViewChild, ViewEncapsulation,} from '@angular/core';
import {
  BeforeLeaveGuard
} from "../../../../window/sidenav/navigation-on-leave-guard";
import {
  DocumentEditPanelComponent
} from "../../document-edit-panel/document-edit-panel.component";

@Component({
  templateUrl: './review-document-panel.component.html',
  styleUrls: ['./review-document-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ReviewDocumentPanelComponent implements BeforeLeaveGuard {

  @ViewChild('reviewDocumentEditor') documentEditor: DocumentEditPanelComponent;

  constructor() {
  }

  isDirty(): boolean {
    return false;
  }

}
