import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CurrencyPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { PanierService } from '../../core/services/panier.service';
import { CommandeService } from '../../core/services/commande.service';

@Component({ selector: 'app-checkout', imports: [ReactiveFormsModule, RouterLink, CurrencyPipe], templateUrl: './checkout.html', styleUrl: './checkout.css' })
export class Checkout implements OnInit {
  private readonly fb = inject(FormBuilder); private readonly router = inject(Router); readonly panierService = inject(PanierService); private readonly commandes = inject(CommandeService);
  readonly erreur = signal(''); readonly envoi = signal(false);
  readonly formulaire = this.fb.nonNullable.group({ destinataire: ['', Validators.required], telephone: ['', Validators.required], adresse: ['', Validators.required], ville: ['', Validators.required], pays: ['Sénégal', Validators.required], modePaiement: ['WAVE' as 'WAVE' | 'ORANGE_MONEY', Validators.required] });
  ngOnInit() { this.panierService.charger().subscribe({ next: (p) => { if (!p.lignes.length) this.router.navigate(['/panier']); }, error: () => this.router.navigate(['/panier']) }); }
  commander() {
    if (this.formulaire.invalid) { this.formulaire.markAllAsTouched(); return; }
    this.envoi.set(true); this.erreur.set(''); const { modePaiement, ...adresseLivraison } = this.formulaire.getRawValue();
    this.commandes.creer({ modePaiement, adresseLivraison }).subscribe({ next: (commande) => { this.panierService.reinitialiser(); if (commande.urlPaiement) window.location.assign(commande.urlPaiement); else this.router.navigate(['/commandes']); }, error: (e) => { this.erreur.set(e.error?.message ?? 'Création de commande impossible.'); this.envoi.set(false); } });
  }
}
