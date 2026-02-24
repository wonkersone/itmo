import { inject } from '@angular/core';
import { Router, UrlTree } from '@angular/router';

export const authGuard = (): boolean | UrlTree => {
  const router = inject(Router);

  const token = localStorage.getItem('authToken');

  if (token) {
    return true;
  } else {
    return router.parseUrl('/');
  }
};
