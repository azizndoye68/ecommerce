import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { AuthActions } from '../../store/auth/auth.actions';

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

        return authService.rafraichir({ refreshToken }).pipe(
          switchMap((response) => {
            localStorage.setItem('token', response.token);
            localStorage.setItem('refreshToken', response.refreshToken);
            const requeteRejouee = req.clone({
              setHeaders: { Authorization: `Bearer ${response.token}` }
            });
            return next(requeteRejouee);
          }),
          catchError((erreurRefresh) => {
            store.dispatch(AuthActions.deconnecterSucces());
            return throwError(() => erreurRefresh);
          })
        );
      }
      return throwError(() => erreur);
    })
  );
};