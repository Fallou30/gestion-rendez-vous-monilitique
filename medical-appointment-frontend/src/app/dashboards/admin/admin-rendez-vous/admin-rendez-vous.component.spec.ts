import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminRendezVousComponent } from './admin-rendez-vous.component';

describe('AdminRendezVousComponent', () => {
  let component: AdminRendezVousComponent;
  let fixture: ComponentFixture<AdminRendezVousComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminRendezVousComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminRendezVousComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
