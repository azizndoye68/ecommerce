import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AuthState } from './auth.reducer';

export const selectAuthState = createFeatureSelector<AuthState>('auth');

export const selectUtilisateur = createSelector(selectAuthState, (state) => state.utilisateur);
export const selectToken = createSelector(selectAuthState, (state) => state.token);
export const selectRefreshToken = createSelector(selectAuthState, (state) => state.refreshToken);
export const selectEstConnecte = createSelector(selectAuthState, (state) => !!state.token);
export const selectRole = createSelector(selectAuthState, (state) => state.utilisateur?.role);
export const selectChargement = createSelector(selectAuthState, (state) => state.chargement);
export const selectErreur = createSelector(selectAuthState, (state) => state.erreur);