import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { AuthResponse, ConnexionRequest, InscriptionRequest } from '../../core/models/auth.model';
import { Utilisateur } from '../../core/models/utilisateur.model';

export const AuthActions = createActionGroup({
  source: 'Auth',
  events: {
    'Connecter': props<{ request: ConnexionRequest }>(),
    'Connecter Succes': props<{ response: AuthResponse }>(),
    'Connecter Echec': props<{ erreur: string }>(),

    'Inscrire': props<{ request: InscriptionRequest }>(),
    'Inscrire Succes': props<{ response: AuthResponse }>(),
    'Inscrire Echec': props<{ erreur: string }>(),

    'Deconnecter': emptyProps(),
    'Deconnecter Succes': emptyProps(),

    'Restaurer Session': emptyProps(),
    'Utilisateur Mis A Jour': props<{ utilisateur: Utilisateur }>()
  }
});
