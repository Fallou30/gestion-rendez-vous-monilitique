import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceptionnisteDashboardComponent } from './receptionniste-dashboard.component';

describe('ReceptionnisteDashboardComponent', () => {
  let component: ReceptionnisteDashboardComponent;
  let fixture: ComponentFixture<ReceptionnisteDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReceptionnisteDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReceptionnisteDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
