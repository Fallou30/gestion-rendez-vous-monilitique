import { TestBed } from '@angular/core/testing';

import { HopitalService } from './hopital.service';

describe('HopitalService', () => {
  let service: HopitalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HopitalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
