import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HopitalServicesComponent } from './hopital-services.component';

describe('HopitalServicesComponent', () => {
  let component: HopitalServicesComponent;
  let fixture: ComponentFixture<HopitalServicesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HopitalServicesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HopitalServicesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
