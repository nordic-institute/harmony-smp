import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TrustStoreComponent } from './trust-store.component';

describe('TrustStoreComponent', () => {
  let component: TrustStoreComponent;
  let fixture: ComponentFixture<TrustStoreComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TrustStoreComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TrustStoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
