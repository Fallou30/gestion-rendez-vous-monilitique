import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceptionnisteFormComponent } from './receptionniste-form.component';

describe('ReceptionnisteFormComponent', () => {
  let component: ReceptionnisteFormComponent;
  let fixture: ComponentFixture<ReceptionnisteFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReceptionnisteFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReceptionnisteFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
