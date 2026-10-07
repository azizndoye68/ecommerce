import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { Panier } from '../models/panier.model';

@Injectable({ providedIn: 'root' })
export class PanierService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/panier`;
  readonly panier = signal<Panier | null>(null);

  charger() { return this.http.get<Panier>(this.baseUrl).pipe(tap((panier) => this.panier.set(panier))); }
  ajouter(produitId: number, quantite = 1) {
    return this.http.post<Panier>(`${this.baseUrl}/lignes`, { produitId, quantite }).pipe(tap((panier) => this.panier.set(panier)));
  }
  modifier(produitId: number, quantite: number) {
    return this.http.patch<Panier>(`${this.baseUrl}/lignes/${produitId}`, { quantite }).pipe(tap((panier) => this.panier.set(panier)));
  }
  supprimer(produitId: number) { return this.http.delete<void>(`${this.baseUrl}/lignes/${produitId}`).pipe(tap(() => this.charger().subscribe())); }
  vider() { return this.http.delete<void>(this.baseUrl).pipe(tap(() => this.panier.set({ id: 0, lignes: [], total: 0 }))); }
  reinitialiser() { this.panier.set(null); }
}
