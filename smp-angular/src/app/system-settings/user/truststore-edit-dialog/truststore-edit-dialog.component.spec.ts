import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {TruststoreEditDialogComponent} from "./truststore-edit-dialog.component";

describe('TruststoreEditDialogComponent', () => {
  let component: TruststoreEditDialogComponent;
  let fixture: ComponentFixture<TruststoreEditDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TruststoreEditDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TruststoreEditDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
