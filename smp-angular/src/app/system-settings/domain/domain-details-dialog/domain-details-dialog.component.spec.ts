import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DomainDetailsDialogComponent } from './domain-details-dialog.component';

describe('DomainDetailsDialogComponent', () => {
  let component: DomainDetailsDialogComponent;
  let fixture: ComponentFixture<DomainDetailsDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DomainDetailsDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DomainDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
