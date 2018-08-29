import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicegroupDetailsDialogComponent } from './servicegroup-details-dialog.component';

describe('ServicegroupDetailsDialogComponent', () => {
  let component: ServicegroupDetailsDialogComponent;
  let fixture: ComponentFixture<ServicegroupDetailsDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServicegroupDetailsDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServicegroupDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
