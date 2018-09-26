import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceGroupMetadataDialogComponent } from './service-group-metadata-dialog.component';

describe('ServicegroupExtensionDialogComponent', () => {
  let component: ServiceGroupMetadataDialogComponent;
  let fixture: ComponentFixture<ServiceGroupMetadataDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServiceGroupMetadataDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceGroupMetadataDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
