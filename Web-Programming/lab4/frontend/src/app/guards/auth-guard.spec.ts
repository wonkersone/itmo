import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { authGuard } from './auth-guard';

describe('authGuard', () => {
  let routerSpy: { parseUrl: jasmine.Spy };

  beforeEach(() => {
    routerSpy = {
      parseUrl: jasmine.createSpy('parseUrl').and.callFake((url: string) => url as any)
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: routerSpy }
      ]
    });
  });

  it('should be created', () => {
    const result = TestBed.runInInjectionContext(() => authGuard());
    expect(result).toBeTruthy();
  });

  it('should return true if user is logged in', () => {
    spyOn(localStorage, 'getItem').and.returnValue('someUser');

    const result = TestBed.runInInjectionContext(() => authGuard());
    expect(result).toBeTrue();
  });

  it('should redirect to root if user is not logged in', () => {
    spyOn(localStorage, 'getItem').and.returnValue(null);

    TestBed.runInInjectionContext(() => authGuard());
    expect(routerSpy.parseUrl).toHaveBeenCalledWith('/');
  });
});
