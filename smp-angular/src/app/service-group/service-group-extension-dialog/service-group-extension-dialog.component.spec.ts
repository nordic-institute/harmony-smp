import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceGroupExtensionDialogComponent } from './service-group-extension-dialog.component';

describe('ServiceGroupExtensionDialogComponent', () => {
  let component: ServiceGroupExtensionDialogComponent;
  let fixture: ComponentFixture<ServiceGroupExtensionDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServiceGroupExtensionDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceGroupExtensionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
