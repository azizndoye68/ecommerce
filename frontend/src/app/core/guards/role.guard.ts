import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { map, take } from 'rxjs';
import { selectRole } from '../../store/auth/auth.selectors';

export const roleGuard = (rolesAutorises: string[]): CanActivateFn => {
  return () => {
    const store = inject(Store);
    const router = inject(Router);

    return store.select(selectRole).pipe(
      take(1),
      map((role) =>
        role && rolesAutorises.includes(role) ? true : router.createUrlTree(['/'])
      )
    );
  };
};