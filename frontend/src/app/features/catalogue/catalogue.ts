import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { CatalogueService } from '../../core/services/catalogue.service';
import { PanierService } from '../../core/services/panier.service';
import { NotificationService } from '../../core/services/notification.service';
import { Categorie, PageResult, Produit } from '../../core/models/catalogue.model';

@Component({ selector: 'app-catalogue', imports: [FormsModule, RouterLink, CurrencyPipe], templateUrl: './catalogue.html', styleUrl: './catalogue.css' })
export class Catalogue implements OnInit {
  private readonly catalogue = inject(CatalogueService);
  private readonly router = inject(Router);
  private readonly panier = inject(PanierService);
  private readonly notification = inject(NotificationService);
  readonly produits = signal<PageResult<Produit> | null>(null);
  readonly categories = signal<Categorie[]>([]);
  readonly chargement = signal(true);
  readonly erreur = signal('');
  recherche = '';
  categorieId?: number;

  ngOnInit() { this.catalogue.categories().subscribe({ next: (c) => this.categories.set(c) }); this.charger(); }
  charger(page = 0) {
    this.chargement.set(true); this.erreur.set('');
    this.catalogue.produits(page, this.categorieId, this.recherche).subscribe({
      next: (p) => { this.produits.set(p); this.chargement.set(false); },
      error: () => { this.erreur.set('Impossible de charger le catalogue.'); this.chargement.set(false); },
    });
  }
  ajouter(produit: Produit) {
    if (!localStorage.getItem('token')) { this.router.navigate(['/connexion'], { queryParams: { retour: '/' } }); return; }
    this.panier.ajouter(produit.id).subscribe({
      next: () => this.notification.afficher(`${produit.nom} ajouté au panier`),
      error: (e) => this.erreur.set(e.error?.message ?? "Impossible d'ajouter ce produit."),
    });
  }
}
