import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Utilisateur } from '../models/utilisateur.model';

export interface InformationsPersonnelles { nom: string; prenom: string; telephone: string; adresse: string; }

@Injectable({ providedIn: 'root' })
export class ProfilService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/utilisateurs/moi`;
  obtenir() { return this.http.get<Utilisateur>(this.url); }
  modifier(informations: InformationsPersonnelles) { return this.http.put<Utilisateur>(this.url, informations); }
  changerMotDePasse(motDePasseActuel: string, nouveauMotDePasse: string) {
    return this.http.put<void>(`${this.url}/mot-de-passe`, { motDePasseActuel, nouveauMotDePasse });
  }
}
