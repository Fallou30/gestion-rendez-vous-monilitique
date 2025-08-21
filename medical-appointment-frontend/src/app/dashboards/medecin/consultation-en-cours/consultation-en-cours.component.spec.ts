import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultationEnCoursComponent } from './consultation-en-cours.component';

describe('ConsultationEnCoursComponent', () => {
  let component: ConsultationEnCoursComponent;
  let fixture: ComponentFixture<ConsultationEnCoursComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultationEnCoursComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultationEnCoursComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
