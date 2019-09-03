import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {CertificateDialogComponent} from "./certificate-dialog.component";

describe('CertificateDialogComponent', () => {
  let component: CertificateDialogComponent;
  let fixture: ComponentFixture<CertificateDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CertificateDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CertificateDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
