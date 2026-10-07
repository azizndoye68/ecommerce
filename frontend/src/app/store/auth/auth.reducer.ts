import { createReducer, on } from '@ngrx/store';
import { AuthActions } from './auth.actions';
import { Utilisateur } from '../../core/models/utilisateur.model';

export interface AuthState {
  utilisateur: Utilisateur | null;
  token: string | null;
  refreshToken: string | null;
  chargement: boolean;
  erreur: string | null;
}

export const initialState: AuthState = {
  utilisateur: null,
  token: null,
  refreshToken: null,
  chargement: false,
  erreur: null
};

export const authReducer = createReducer(
  initialState,

  on(AuthActions.connecter, AuthActions.inscrire, (state) => ({
    ...state,
    chargement: true,
    erreur: null
  })),

  on(AuthActions.connecterSucces, AuthActions.inscrireSucces, (state, { response }) => ({
    ...state,
    chargement: false,
    token: response.token,
    refreshToken: response.refreshToken,
    utilisateur: {
      email: response.email,
      nom: response.nom,
      prenom: response.prenom,
      role: response.role
    }
  })),

  on(AuthActions.connecterEchec, AuthActions.inscrireEchec, (state, { erreur }) => ({
    ...state,
    chargement: false,
    erreur
  })),

  on(AuthActions.deconnecterSucces, () => initialState),

  on(AuthActions.restaurerSession, (state) => {
    const token = localStorage.getItem('token');
    const refreshToken = localStorage.getItem('refreshToken');
    const utilisateurStocke = localStorage.getItem('utilisateur');

    if (token && refreshToken && utilisateurStocke) {
      return {
        ...state,
        token,
        refreshToken,
        utilisateur: JSON.parse(utilisateurStocke)
      };
    }
    return state;
  })
);