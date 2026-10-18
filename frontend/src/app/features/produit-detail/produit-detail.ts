import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { CatalogueService } from '../../core/services/catalogue.service';
import { PanierService } from '../../core/services/panier.service';
import { NotificationService } from '../../core/services/notification.service';
import { Produit } from '../../core/models/catalogue.model';

@Component({ selector: 'app-produit-detail', imports: [RouterLink, CurrencyPipe], templateUrl: './produit-detail.html', styleUrl: './produit-detail.css' })
export class ProduitDetail implements OnInit {
  private readonly route = inject(ActivatedRoute); private readonly router = inject(Router); private readonly catalogue = inject(CatalogueService); private readonly panier = inject(PanierService); private readonly notification = inject(NotificationService);
  readonly produit = signal<Produit | null>(null); readonly erreur = signal('');
  ngOnInit() { const id = Number(this.route.snapshot.paramMap.get('id')); if (!id) { this.router.navigate(['/']); return; } this.catalogue.produit(id).subscribe({ next: (p) => this.produit.set(p), error: () => this.erreur.set('Produit introuvable.') }); }
  ajouter() { const produit = this.produit(); if (!produit) return; if (!localStorage.getItem('token')) { this.router.navigate(['/connexion'], { queryParams: { retour: `/produits/${produit.id}` } }); return; } this.panier.ajouter(produit.id).subscribe({ next: () => { this.notification.afficher(`${produit.nom} ajouté au panier`); this.router.navigate(['/panier']); }, error: (e) => this.erreur.set(e.error?.message ?? "Impossible d'ajouter le produit.") }); }
}
