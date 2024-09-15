import {Component, ViewChild, ViewEncapsulation,} from '@angular/core';
import {
  BeforeLeaveGuard
} from "../../../window/sidenav/navigation-on-leave-guard";
import {
  DocumentEditPanelComponent
} from "../../../common/panels/document-edit-panel/document-edit-panel.component";

@Component({
  templateUrl: './subresource-document-panel.component.html',
  styleUrls: ['./subresource-document-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SubresourceDocumentPanelComponent implements BeforeLeaveGuard {

  @ViewChild('subresourceDocumentEditor') documentEditor: DocumentEditPanelComponent;

  constructor() {

  }

  isDirty(): boolean {
    return this.documentEditor.isDirty();
  }

}
