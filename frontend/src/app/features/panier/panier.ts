import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { PanierService } from '../../core/services/panier.service';

@Component({ selector: 'app-panier', imports: [RouterLink, CurrencyPipe], templateUrl: './panier.html', styleUrl: './panier.css' })
export class PanierPage implements OnInit {
  readonly panierService = inject(PanierService); readonly erreur = signal(''); readonly chargement = signal(true);
  ngOnInit() { this.panierService.charger().subscribe({ next: () => this.chargement.set(false), error: (e) => { this.erreur.set(e.error?.message ?? 'Impossible de charger le panier.'); this.chargement.set(false); } }); }
  modifier(id: number, quantite: number) { if (quantite < 1) return this.supprimer(id); this.panierService.modifier(id, quantite).subscribe({ error: (e) => this.erreur.set(e.error?.message ?? 'Modification impossible.') }); }
  supprimer(id: number) { this.panierService.supprimer(id).subscribe({ error: (e) => this.erreur.set(e.error?.message ?? 'Suppression impossible.') }); }
}
