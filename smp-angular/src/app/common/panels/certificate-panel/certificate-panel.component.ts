import {Component, Input} from '@angular/core';
import {CertificateRo} from "../../model/certificate-ro.model";
import {DateTimeService} from "../../services/date-time.service";
import {MatTableDataSource} from "@angular/material/table";
import {CertificateExtensionRo} from "../../model/certificate-extension-ro.model";
import {SmpTableColDef} from "../../components/smp-table/smp-table-coldef.model";

@Component({
  selector: 'certificate-panel',
  templateUrl: './certificate-panel.component.html',
  styleUrls: ['./certificate-panel.component.scss'],
  standalone: false
})
export class CertificatePanelComponent {

  _certificate: CertificateRo = null;
  extensionData: MatTableDataSource<CertificateExtensionRo> = new MatTableDataSource();
  selectedExtension: CertificateExtensionRo;
  displayedExtensionColumns: string[] = ['oid', 'name', 'critical'];
  extensionColumns: SmpTableColDef[];


  constructor(private dateTimeService: DateTimeService) {

    this.extensionColumns = [
      {
        columnDef: 'oid',
        header: 'certificate.panel.tab.extension.table.oid',
        cell: (row: CertificateExtensionRo) => row.oid,
        style: 'flex-grow: 0;flex-basis:110px;'
      } as SmpTableColDef,
      {
        columnDef: 'name',
        header: 'certificate.panel.tab.extension.table.name',
        cell: (row: CertificateExtensionRo) => row.name,
        style: 'flex-grow: 1;'
      } as SmpTableColDef,
      {
        columnDef: 'critical',
        header: 'certificate.panel.tab.extension.table.critical',
        cell: (row: CertificateExtensionRo) => row.critical,
        style: 'flex-grow: 0;flex-basis:80px;'
      } as SmpTableColDef,

    ];
  }

  applyExtensionFilter(filterValue: string) {
    this.extensionData.filter = filterValue?.trim().toLowerCase();

    if (this.extensionData.paginator) {
      this.extensionData.paginator.firstPage();
    }
    this.selectedExtension= this.extensionData.filteredData.length > 0 ? this.extensionData.filteredData[0] : null;
  }

  get certificate(): CertificateRo {
    return this._certificate;
  }

  get selectedExtensionDescription(): string {
    return this.selectedExtension ? this.selectedExtension.name + " (" + this.selectedExtension.oid + ")" : 'No extension selected';
  }

  @Input() set certificate(value: CertificateRo) {
    this._certificate = value;
    this.extensionData.data = value?.extensions || [];
    if (this.extensionData.data.length > 0) {
      this.selectedExtension = this.extensionData.data[0];
    } else {
        this.selectedExtension = null;
    }
  }


  @Input() set selectedExtensionRow(value: CertificateExtensionRo) {
    console.log("selectedExtensionRow", value);
    this.selectedExtension = value;
  }

  get selectedExtensionRow(): any {
    return this.selectedExtension;
  }

  onSelectExtensionRow(value: CertificateExtensionRo) {
    return this.selectedExtension = value;
  }

  public formatDate(date: Date): string {
    return this.dateTimeService.formatDateTimeForUserLocal(date);
  }

  get certificateValidFromFormattedDate(): string {
    return this.formatDate(this._certificate?.validFrom);
  }

  get certificateValidToFormattedDate(): string {
    return this.formatDate(this._certificate?.validTo);
  }

  clearData() {
    this._certificate = null;
    this.extensionData.data = [];
    this.selectedExtension = null;
  }
}
