import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientWorkspaceComponent } from './patient-workspace.component';

describe('PatientWorkspaceComponent', () => {
  let component: PatientWorkspaceComponent;
  let fixture: ComponentFixture<PatientWorkspaceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientWorkspaceComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientWorkspaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
