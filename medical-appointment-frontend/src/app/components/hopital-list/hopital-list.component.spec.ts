import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HopitalListComponent } from './hopital-list.component';

describe('HopitalListComponent', () => {
  let component: HopitalListComponent;
  let fixture: ComponentFixture<HopitalListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HopitalListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HopitalListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
