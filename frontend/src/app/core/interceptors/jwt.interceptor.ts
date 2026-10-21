import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, catchError, finalize, shareReplay, switchMap, tap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { AuthActions } from '../../store/auth/auth.actions';
import { AuthResponse } from '../models/auth.model';

// Plusieurs appels peuvent expirer en même temps. Ils partagent un seul
// renouvellement afin de ne pas consommer plusieurs fois le même refresh token.
let rafraichissementEnCours$: Observable<AuthResponse> | null = null;

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const store = inject(Store);

  const token = localStorage.getItem('token');
  const estRequeteAuth = req.url.includes('/auth/');

  const requeteAvecToken = token && !estRequeteAuth
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(requeteAvecToken).pipe(
    catchError((erreur: HttpErrorResponse) => {
      if (erreur.status === 401 && !estRequeteAuth) {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          store.dispatch(AuthActions.deconnecterSucces());
          return throwError(() => erreur);
        }

        if (!rafraichissementEnCours$) {
          rafraichissementEnCours$ = authService.rafraichir({ refreshToken }).pipe(
            tap((response) => {
              localStorage.setItem('token', response.token);
              localStorage.setItem('refreshToken', response.refreshToken);
              store.dispatch(AuthActions.jetonRafraichi({ response }));
            }),
            catchError((erreurRefresh) => {
              store.dispatch(AuthActions.deconnecterSucces());
              return throwError(() => erreurRefresh);
            }),
            finalize(() => { rafraichissementEnCours$ = null; }),
            shareReplay({ bufferSize: 1, refCount: false })
          );
        }

        return rafraichissementEnCours$.pipe(
          switchMap((response) => next(req.clone({
            setHeaders: { Authorization: `Bearer ${response.token}` }
          })))
        );
      }
      return throwError(() => erreur);
    })
  );
};
