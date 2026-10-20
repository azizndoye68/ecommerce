import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AuthResponse,
  ConnexionRequest,
  InscriptionRequest,
  RefreshTokenRequest
} from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/auth`;

  inscrire(request: InscriptionRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/inscription`, request);
  }

  connecter(request: ConnexionRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/connexion`, request);
  }

  demanderReinitialisation(email: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.baseUrl}/mot-de-passe-oublie`, { email });
  }

  reinitialiserMotDePasse(token: string, nouveauMotDePasse: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/reinitialiser-mot-de-passe`, { token, nouveauMotDePasse });
  }

  rafraichir(request: RefreshTokenRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/rafraichir`, request);
  }

  deconnecter(token: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/deconnexion`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }
}
