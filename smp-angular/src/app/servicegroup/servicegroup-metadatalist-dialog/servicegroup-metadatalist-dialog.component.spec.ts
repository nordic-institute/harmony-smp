import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicegroupMetadatalistDialogComponent } from './servicegroup-metadatalist-dialog.component';

describe('ServicegroupExtensionDialogComponent', () => {
  let component: ServicegroupMetadatalistDialogComponent;
  let fixture: ComponentFixture<ServicegroupMetadatalistDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServicegroupMetadatalistDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServicegroupMetadatalistDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
