import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {KeystoreCertificateDialogComponent} from "./keystore-certificate-dialog.component";

describe('KeystoreCertificateDialogComponent', () => {
  let component: KeystoreCertificateDialogComponent;
  let fixture: ComponentFixture<KeystoreCertificateDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ KeystoreCertificateDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(KeystoreCertificateDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
