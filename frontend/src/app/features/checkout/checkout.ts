import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { PanierService } from '../../core/services/panier.service';
import { CommandeService } from '../../core/services/commande.service';
import { environment } from '../../environments/environment';
import { PrixCfaPipe } from '../../shared/pipes/prix-cfa.pipe';

@Component({ selector: 'app-checkout', imports: [ReactiveFormsModule, RouterLink, PrixCfaPipe], templateUrl: './checkout.html', styleUrl: './checkout.css' })
export class Checkout implements OnInit {
  private readonly fb = inject(FormBuilder); private readonly router = inject(Router); readonly panierService = inject(PanierService); private readonly commandes = inject(CommandeService);
  readonly erreur = signal(''); readonly envoi = signal(false);
  readonly paiementSimule = environment.paiementSimule;
  readonly formulaire = this.fb.nonNullable.group({ destinataire: ['', Validators.required], telephone: ['', Validators.required], adresse: ['', Validators.required], ville: ['', Validators.required], pays: ['Sénégal', Validators.required], modePaiement: ['WAVE' as 'WAVE' | 'ORANGE_MONEY' | 'SIMULE', Validators.required] });
  ngOnInit() { this.panierService.charger().subscribe({ next: (p) => { if (!p.lignes.length) this.router.navigate(['/panier']); }, error: () => this.router.navigate(['/panier']) }); }
  commander() {
    if (this.formulaire.invalid) { this.formulaire.markAllAsTouched(); return; }
    this.envoi.set(true); this.erreur.set(''); const { modePaiement, ...adresseLivraison } = this.formulaire.getRawValue();
    this.commandes.creer({ modePaiement, adresseLivraison }).subscribe({ next: (commande) => {
      this.panierService.reinitialiser();
      if (commande.urlPaiement) window.location.assign(commande.urlPaiement);
      else if (commande.modePaiement === 'SIMULE') this.commandes.confirmer(commande.id).subscribe({ next: () => this.router.navigate(['/commandes']), error: (e) => { this.erreur.set(e.error?.message ?? 'Confirmation simulée impossible.'); this.envoi.set(false); } });
      else this.router.navigate(['/commandes']);
    }, error: (e) => { this.erreur.set(e.error?.message ?? 'Création de commande impossible.'); this.envoi.set(false); } });
  }
}
