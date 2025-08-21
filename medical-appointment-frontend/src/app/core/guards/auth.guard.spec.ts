import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { AuthGuard } from './auth.guard';

describe('authGuard', () => {
  let guard: AuthGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.runInInjectionContext(() => TestBed.inject(AuthGuard));
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  // Removed redundant or incorrect test for 'executeGuard' as it is not defined.
});
