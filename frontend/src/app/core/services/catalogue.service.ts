import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Categorie, PageResult, Produit } from '../models/catalogue.model';

@Injectable({ providedIn: 'root' })
export class CatalogueService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  produits(page = 0, categorieId?: number, recherche?: string) {
    let params = new HttpParams().set('page', page).set('size', 12);
    if (recherche?.trim()) {
      params = params.set('nom', recherche.trim());
      return this.http.get<PageResult<Produit>>(`${this.baseUrl}/produits/recherche`, { params });
    }
    if (categorieId) return this.http.get<PageResult<Produit>>(`${this.baseUrl}/produits/categorie/${categorieId}`, { params });
    return this.http.get<PageResult<Produit>>(`${this.baseUrl}/produits`, { params });
  }

  categories() { return this.http.get<Categorie[]>(`${this.baseUrl}/categories`); }
  produit(id: number) { return this.http.get<Produit>(`${this.baseUrl}/produits/${id}`); }
}
