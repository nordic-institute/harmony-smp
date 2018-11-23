import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpiredPasswordDialogComponent } from './expired-password-dialog.component';

describe('ExpiredPasswordDialogComponent', () => {
  let component: ExpiredPasswordDialogComponent;
  let fixture: ComponentFixture<ExpiredPasswordDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExpiredPasswordDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExpiredPasswordDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
