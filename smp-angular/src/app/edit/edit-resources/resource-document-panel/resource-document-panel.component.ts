import {Component, ViewChild, ViewEncapsulation,} from '@angular/core';
import {
  BeforeLeaveGuard
} from "../../../window/sidenav/navigation-on-leave-guard";
import {
  DocumentEditPanelComponent
} from "../../../common/panels/document-edit-panel/document-edit-panel.component";

@Component({
  templateUrl: './resource-document-panel.component.html',
  styleUrls: ['./resource-document-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ResourceDocumentPanelComponent implements BeforeLeaveGuard {

  @ViewChild('documentEditor') documentEditor: DocumentEditPanelComponent;

  constructor() {

  }

  isDirty(): boolean {
    return this.documentEditor.isDirty();
  }

}
