import {Component, Input} from '@angular/core';
import {SecurityService} from '../../security/security.service';
import {DomainService} from '../../security/domain.service';
import {Domain} from '../../security/domain.model';
import {MatDialog} from '@angular/material/dialog';
import {CancelDialogComponent} from '../dialogs/cancel-dialog/cancel-dialog.component';
import {firstValueFrom} from "rxjs";

@Component({
  selector: 'domain-selector',
  templateUrl: './domain-selector.component.html',
  styleUrls: ['./domain-selector.component.css']
})
export class DomainSelectorComponent {

  showDomains: boolean;
  currentDomainCode: string;
  domainCode: string;
  domains: Domain[];

  @Input()
  currentComponent: any;

  constructor (private domainService: DomainService, private securityService: SecurityService, private dialog: MatDialog) {
  }

  changeDomain () {
    let canChangeDomain = Promise.resolve(true);
    if (this.currentComponent?.isDirty && this.currentComponent.isDirty()) {
      canChangeDomain = firstValueFrom(this.dialog.open(CancelDialogComponent).afterClosed());
    }

    canChangeDomain.then((canChange: boolean) => {
      if (!canChange) throw false;

      let domain = this.domains.find(d => d.code == this.domainCode);
      this.domainService.setCurrentDomain(domain).then(() => {
        if (this.currentComponent.ngOnInit)
          this.currentComponent.ngOnInit();
      });

    }).catch(() => { // domain not changed -> reset the combo value
      this.domainCode = this.currentDomainCode;
    });
  }
}
