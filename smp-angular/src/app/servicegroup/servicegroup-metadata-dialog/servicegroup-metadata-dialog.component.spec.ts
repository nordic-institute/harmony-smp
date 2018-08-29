import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicegroupMetadataDialogComponent } from './servicegroup-metadata-dialog.component';

describe('ServicegroupExtensionDialogComponent', () => {
  let component: ServicegroupMetadataDialogComponent;
  let fixture: ComponentFixture<ServicegroupMetadataDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServicegroupMetadataDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServicegroupMetadataDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
