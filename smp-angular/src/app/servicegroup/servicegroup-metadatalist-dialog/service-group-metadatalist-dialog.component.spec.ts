import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceGroupMetadatalistDialogComponent } from './service-group-metadatalist-dialog.component';

describe('ServicegroupExtensionDialogComponent', () => {
  let component: ServiceGroupMetadatalistDialogComponent;
  let fixture: ComponentFixture<ServiceGroupMetadatalistDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServiceGroupMetadatalistDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceGroupMetadatalistDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
