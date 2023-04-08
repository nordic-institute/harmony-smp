import {Component, Input,} from '@angular/core';
import {DomainRo} from "../../domain/domain-ro.model";
import {FormBuilder, FormGroup} from "@angular/forms";
import {ExtensionService} from "../../admin-extension/extension.service";
import {ExtensionRo} from "../../admin-extension/extension-ro.model";
import {ResourceDefinitionRo} from "../../admin-extension/resource-definition-ro.model";


@Component({
  selector: 'domain-resource-type-panel',
  templateUrl: './domain-resource-type-panel.component.html',
  styleUrls: ['./domain-resource-type-panel.component.scss']
})
export class DomainResourceTypePanelComponent {

  _domain: DomainRo = null;

  domiSMPResourceDefinitions: ResourceDefinitionRo[] = [];
  domainForm: FormGroup;


  get domain(): DomainRo {
    return this._domain;
  }

  @Input() set domain(value: DomainRo) {
    this._domain = value;

  }

  constructor(
    private formBuilder: FormBuilder,
    extensionService: ExtensionService
  ) {
    extensionService.onExtensionsUpdatesEvent().subscribe(updatedExtensions => {
        this.updateExtensions(updatedExtensions);
      }
    );

    extensionService.getExtensions();
  }

  updateExtensions(extensions: ExtensionRo[]) {

    let allResourceDefinition: ResourceDefinitionRo[] = [];
    extensions.forEach(ext => allResourceDefinition.push(...ext.resourceDefinitions))

    this.domiSMPResourceDefinitions = allResourceDefinition;
  }

  onSaveClicked() {

  }
}
