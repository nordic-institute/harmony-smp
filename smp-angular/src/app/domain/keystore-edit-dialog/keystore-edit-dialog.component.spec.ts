import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {KeystoreEditDialogComponent} from "./keystore-edit-dialog.component";

describe('KeystoreEditDialogComponent', () => {
  let component: KeystoreEditDialogComponent;
  let fixture: ComponentFixture<KeystoreEditDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ KeystoreEditDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(KeystoreEditDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
