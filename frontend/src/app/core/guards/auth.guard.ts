import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { map, take } from 'rxjs';
import { selectEstConnecte } from '../../store/auth/auth.selectors';

export const authGuard: CanActivateFn = (route, state) => {
  const store = inject(Store);
  const router = inject(Router);

  const sessionLocale = !!localStorage.getItem('token');
  return store.select(selectEstConnecte).pipe(
    take(1),
    map((estConnecte) =>
      (estConnecte || sessionLocale)
        ? true
        : router.createUrlTree(['/connexion'], { queryParams: { retour: state.url } })
    )
  );
};
