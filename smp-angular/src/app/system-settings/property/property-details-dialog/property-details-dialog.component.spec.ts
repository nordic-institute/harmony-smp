import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PropertyDetailsDialogComponent } from './property-details-dialog.component';

describe('DomainDetailsDialogComponent', () => {
  let component: PropertyDetailsDialogComponent;
  let fixture: ComponentFixture<PropertyDetailsDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PropertyDetailsDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PropertyDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
