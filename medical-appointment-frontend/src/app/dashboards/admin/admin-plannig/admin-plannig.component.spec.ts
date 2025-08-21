import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPlannigComponent } from './admin-plannig.component';

describe('AdminPlannigComponent', () => {
  let component: AdminPlannigComponent;
  let fixture: ComponentFixture<AdminPlannigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminPlannigComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminPlannigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
