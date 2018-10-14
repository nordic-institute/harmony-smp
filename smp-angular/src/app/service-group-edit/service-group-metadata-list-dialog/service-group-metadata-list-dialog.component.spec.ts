import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceGroupMetadataListDialogComponent } from './service-group-metadata-list-dialog.component';

describe('ServiceGroupMetadataListDialogComponent', () => {
  let component: ServiceGroupMetadataListDialogComponent;
  let fixture: ComponentFixture<ServiceGroupMetadataListDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServiceGroupMetadataListDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceGroupMetadataListDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
