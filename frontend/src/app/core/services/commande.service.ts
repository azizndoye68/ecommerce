import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Commande, CreerCommandeRequest } from '../models/commande.model';

@Injectable({ providedIn: 'root' })
export class CommandeService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/commandes`;
  creer(request: CreerCommandeRequest) { return this.http.post<Commande>(this.baseUrl, request); }
  mesCommandes() { return this.http.get<Commande[]>(this.baseUrl); }
  commande(id: number) { return this.http.get<Commande>(`${this.baseUrl}/${id}`); }
  annuler(id: number) { return this.http.delete<void>(`${this.baseUrl}/${id}`); }
  confirmer(id: number) { return this.http.post<Commande>(`${this.baseUrl}/${id}/paiement/confirmer`, {}); }
}
