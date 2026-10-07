import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe, CurrencyPipe, TitleCasePipe } from '@angular/common';
import { CommandeService } from '../../core/services/commande.service';
import { Commande } from '../../core/models/commande.model';

@Component({ selector: 'app-commandes', imports: [DatePipe, CurrencyPipe, TitleCasePipe], templateUrl: './commandes.html', styleUrl: './commandes.css' })
export class Commandes implements OnInit {
  private readonly service = inject(CommandeService); readonly commandes = signal<Commande[]>([]); readonly erreur = signal(''); readonly chargement = signal(true);
  ngOnInit() { this.charger(); }
  charger() { this.service.mesCommandes().subscribe({ next: (commandes) => { this.commandes.set(commandes); this.chargement.set(false); }, error: () => { this.erreur.set('Impossible de charger vos commandes.'); this.chargement.set(false); } }); }
  annuler(commande: Commande) { if (!confirm(`Annuler la commande #${commande.id} ?`)) return; this.service.annuler(commande.id).subscribe({ next: () => this.charger(), error: (e) => this.erreur.set(e.error?.message ?? 'Annulation impossible.') }); }
  payer(commande: Commande) { if (commande.urlPaiement) window.location.assign(commande.urlPaiement); }
}
