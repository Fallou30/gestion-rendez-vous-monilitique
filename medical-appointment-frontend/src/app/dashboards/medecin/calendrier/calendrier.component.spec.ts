import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalendrierMedecinComponent } from './calendrier.component';

describe('CalendrierComponent', () => {
  let component: CalendrierMedecinComponent;
  let fixture: ComponentFixture<CalendrierMedecinComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalendrierMedecinComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CalendrierMedecinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
