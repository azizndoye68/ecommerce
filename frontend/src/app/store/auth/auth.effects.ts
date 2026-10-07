import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap, tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthActions } from './auth.actions';
import { AuthService } from '../../core/services/auth.service';

@Injectable()
export class AuthEffects {
  private actions$ = inject(Actions);
  private authService = inject(AuthService);
  private router = inject(Router);

  connecter$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.connecter),
      switchMap(({ request }) =>
        this.authService.connecter(request).pipe(
          map((response) => AuthActions.connecterSucces({ response })),
          catchError((err) =>
            of(AuthActions.connecterEchec({ erreur: err.error?.message || 'Échec de connexion' })),
          ),
        ),
      ),
    ),
  );

  inscrire$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.inscrire),
      switchMap(({ request }) =>
        this.authService.inscrire(request).pipe(
          map((response) => AuthActions.inscrireSucces({ response })),
          catchError((err) =>
            of(
              AuthActions.inscrireEchec({ erreur: err.error?.message || "Échec de l'inscription" }),
            ),
          ),
        ),
      ),
    ),
  );

  connexionReussie$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.connecterSucces, AuthActions.inscrireSucces),
        tap(({ response }) => {
          localStorage.setItem('token', response.token);
          localStorage.setItem('refreshToken', response.refreshToken);
          localStorage.setItem(
            'utilisateur',
            JSON.stringify({
              email: response.email,
              nom: response.nom,
              prenom: response.prenom,
              role: response.role,
            }),
          );

          const params = new URLSearchParams(window.location.search);
          const retour = params.get('retour');

          if (retour) {
            this.router.navigateByUrl(retour);
          } else if (response.role === 'ADMIN' || response.role === 'SUPER_ADMIN') {
            this.router.navigate(['/admin']);
          } else {
            this.router.navigate(['/']);
          }
        }),
      ),
    { dispatch: false },
  );

  deconnecter$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.deconnecter),
      switchMap(() => {
        const token = localStorage.getItem('token');
        const deconnexion$ = token ? this.authService.deconnecter(token) : of(void 0);
        return deconnexion$.pipe(
          map(() => AuthActions.deconnecterSucces()),
          catchError(() => of(AuthActions.deconnecterSucces())),
        );
      }),
    ),
  );

  deconnexionReussie$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.deconnecterSucces),
        tap(() => {
          localStorage.removeItem('token');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('utilisateur');
          this.router.navigate(['/connexion']);
        }),
      ),
    { dispatch: false },
  );
}
