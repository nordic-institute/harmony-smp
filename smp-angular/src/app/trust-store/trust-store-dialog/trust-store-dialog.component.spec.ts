import {async, ComponentFixture, TestBed} from "@angular/core/testing";

import {TrustStoreDialogComponent} from "./trust-store-dialog.component";

describe('TrustStoreDialogComponent', () => {
  let component: TrustStoreDialogComponent;
  let fixture: ComponentFixture<TrustStoreDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TrustStoreDialogComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TrustStoreDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
