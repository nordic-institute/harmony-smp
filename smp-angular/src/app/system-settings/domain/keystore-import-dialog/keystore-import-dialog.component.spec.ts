import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {KeystoreImportDialogComponent} from "./keystore-import-dialog.component";

describe('KeystoreImportDialogComponent', () => {
  let component: KeystoreImportDialogComponent;
  let fixture: ComponentFixture<KeystoreImportDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ KeystoreImportDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(KeystoreImportDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
