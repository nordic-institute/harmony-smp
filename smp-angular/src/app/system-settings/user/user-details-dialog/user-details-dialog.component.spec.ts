import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {UserDetailsDialogComponent} from './user-details-dialog.component';

describe('UserDetailsDialogComponent', () => {
  let component: UserDetailsDialogComponent;
  let fixture: ComponentFixture<UserDetailsDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserDetailsDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
