import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceGroupDetailsDialogComponent } from './service-group-details-dialog.component';

describe('ServiceGroupDetailsDialogComponent', () => {
  let component: ServiceGroupDetailsDialogComponent;
  let fixture: ComponentFixture<ServiceGroupDetailsDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServiceGroupDetailsDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceGroupDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
