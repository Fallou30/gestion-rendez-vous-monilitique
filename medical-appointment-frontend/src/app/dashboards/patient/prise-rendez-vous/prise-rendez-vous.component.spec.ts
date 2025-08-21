import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PriseRendezVousComponent } from './prise-rendez-vous.component';

describe('PriseRendezVousComponent', () => {
  let component: PriseRendezVousComponent;
  let fixture: ComponentFixture<PriseRendezVousComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PriseRendezVousComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PriseRendezVousComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
