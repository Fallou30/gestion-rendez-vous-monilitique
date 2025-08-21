import { TestBed } from '@angular/core/testing';
import { CanActivate } from '@angular/router';

import { AdminGuard } from './admin.guard';

describe('AdminGuard', () => {
  let guard: AdminGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AdminGuard,
        { provide: 'AuthService', useValue: { isAdmin: () => true } },
        { provide: 'Router', useValue: { navigate: jasmine.createSpy('navigate') } }
      ]
    });
    guard = TestBed.inject(AdminGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
