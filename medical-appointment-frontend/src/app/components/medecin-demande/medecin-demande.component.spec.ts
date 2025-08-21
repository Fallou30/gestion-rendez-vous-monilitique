import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MedecinDemandeComponent } from './medecin-demande.component';

describe('MedecinDemandeComponent', () => {
  let component: MedecinDemandeComponent;
  let fixture: ComponentFixture<MedecinDemandeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MedecinDemandeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MedecinDemandeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
