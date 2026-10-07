import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Categorie, Produit } from '../models/catalogue.model';
import { Commande, StatutCommande } from '../models/commande.model';
import { Utilisateur } from '../models/utilisateur.model';

export interface CreerAdministrateurRequest { nom: string; prenom: string; email: string; motDePasse: string; telephone?: string; adresse?: string; }

@Injectable({ providedIn: 'root' })
export class AdministrationService {
  private readonly http = inject(HttpClient); private readonly api = environment.apiUrl;
  creerProduit(produit: Omit<Produit, 'id' | 'categorieNom'>) { return this.http.post<Produit>(`${this.api}/produits`, produit); }
  modifierProduit(id: number, produit: Omit<Produit, 'id' | 'categorieNom'>) { return this.http.put<Produit>(`${this.api}/produits/${id}`, produit); }
  supprimerProduit(id: number) { return this.http.delete<void>(`${this.api}/produits/${id}`); }
  creerCategorie(categorie: Omit<Categorie, 'id'>) { return this.http.post<Categorie>(`${this.api}/categories`, categorie); }
  supprimerCategorie(id: number) { return this.http.delete<void>(`${this.api}/categories/${id}`); }
  commandes() { return this.http.get<Commande[]>(`${this.api}/admin/commandes`); }
  changerStatutCommande(id: number, statut: StatutCommande) { return this.http.patch<Commande>(`${this.api}/admin/commandes/${id}/statut`, { statut }); }
  administrateurs() { return this.http.get<Utilisateur[]>(`${this.api}/admin/administrateurs`); }
  creerAdministrateur(request: CreerAdministrateurRequest) { return this.http.post<Utilisateur>(`${this.api}/admin/administrateurs`, request); }
  changerStatutAdministrateur(id: number, actif: boolean) { return this.http.patch<Utilisateur>(`${this.api}/admin/administrateurs/${id}/statut`, { actif }); }
}
