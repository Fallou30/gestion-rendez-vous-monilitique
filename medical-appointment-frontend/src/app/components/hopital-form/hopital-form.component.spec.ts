import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HopitalFormComponent } from './hopital-form.component';

describe('HopitalFormComponent', () => {
  let component: HopitalFormComponent;
  let fixture: ComponentFixture<HopitalFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HopitalFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HopitalFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
