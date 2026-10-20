import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { DatePipe, CurrencyPipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { CommandeService } from '../../core/services/commande.service';
import { Commande } from '../../core/models/commande.model';

@Component({ selector: 'app-commandes', imports: [DatePipe, CurrencyPipe], templateUrl: './commandes.html', styleUrl: './commandes.css' })
export class Commandes implements OnInit {
  private readonly service = inject(CommandeService);
  private readonly route = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);
  private readonly debutRetourPaiement = Date.now();
  private attenteWebhook?: ReturnType<typeof setTimeout>;
  private tentativesWebhook = 0;
  readonly commandes = signal<Commande[]>([]);
  readonly erreur = signal('');
  readonly chargement = signal(true);
  readonly retourPaiement = signal<'success' | 'cancel' | null>(null);
  readonly messagePaiement = signal('');
  readonly commandeEnVerification = signal<number | null>(null);
  readonly etapes = [
    { statut: 'PAYEE', label: 'Paiement confirmé' },
    { statut: 'EN_PREPARATION', label: 'Préparation' },
    { statut: 'EXPEDIEE', label: 'Expédition' },
    { statut: 'LIVREE', label: 'Livraison' },
  ] as const;

  libelleStatut(statut: string) {
    return ({ EN_ATTENTE_PAIEMENT: 'En attente de paiement', PAYEE: 'Payée', EN_PREPARATION: 'En préparation', EXPEDIEE: 'Expédiée', LIVREE: 'Livrée', ANNULEE: 'Annulée' } as Record<string, string>)[statut] ?? statut;
  }

  progression(statut: string) {
    if (statut === 'ANNULEE' || statut === 'EN_ATTENTE_PAIEMENT') return -1;
    return this.etapes.findIndex((etape) => etape.statut === statut);
  }

  constructor() {
    this.destroyRef.onDestroy(() => {
      if (this.attenteWebhook) clearTimeout(this.attenteWebhook);
    });
  }

  ngOnInit() {
    const retour = this.route.snapshot.queryParamMap.get('paiement');
    if (retour === 'success' || retour === 'cancel') {
      this.retourPaiement.set(retour);
      this.messagePaiement.set(retour === 'success'
        ? 'Retour de paiement reçu. Nous vérifions la confirmation sécurisée…'
        : 'Le paiement a été interrompu. La commande reste en attente si aucun paiement n’a été confirmé.');
    }
    this.charger();
  }

  charger() {
    this.service.mesCommandes().subscribe({
      next: (commandes) => {
        this.commandes.set(commandes);
        this.chargement.set(false);
        this.verifierRetourPaiement(commandes);
      },
      error: () => {
        this.erreur.set('Impossible de charger vos commandes.');
        this.chargement.set(false);
      },
    });
  }

  private verifierRetourPaiement(commandes: Commande[]) {
    if (this.retourPaiement() !== 'success') return;
    const plusRecent = commandes.find((commande) =>
      Date.parse(commande.creeLe) >= this.debutRetourPaiement - 60 * 60_000
      && (commande.modePaiement === 'WAVE' || commande.modePaiement === 'ORANGE_MONEY'));

    if (!plusRecent) {
      this.messagePaiement.set('Retour reçu. Le statut de la commande sera visible ici après confirmation par UniTech Pay.');
      return;
    }
    if (plusRecent.statut !== 'EN_ATTENTE_PAIEMENT') {
      this.commandeEnVerification.set(null);
      this.messagePaiement.set(plusRecent.statut === 'ANNULEE'
        ? 'Cette commande a été annulée; aucun paiement n’a été confirmé.'
        : 'Paiement confirmé, merci pour votre commande !');
      return;
    }

    this.commandeEnVerification.set(plusRecent.id);
    if (Date.now() - this.debutRetourPaiement >= 60_000 || this.tentativesWebhook >= 20) {
      this.commandeEnVerification.set(null);
      this.messagePaiement.set('Le paiement n’est pas encore confirmé par le serveur. Ne payez pas une seconde fois; vérifiez que le webhook UniTech Pay est configuré, puis actualisez cette page.');
      return;
    }

    this.messagePaiement.set('Paiement effectué. La confirmation sécurisée peut prendre quelques instants…');
    this.tentativesWebhook++;
    this.attenteWebhook = setTimeout(() => this.charger(), 3_000);
  }

  annuler(commande: Commande) { if (!confirm(`Annuler la commande #${commande.id} ?`)) return; this.service.annuler(commande.id).subscribe({ next: () => this.charger(), error: (e) => this.erreur.set(e.error?.message ?? 'Annulation impossible.') }); }
  payer(commande: Commande) { if (commande.urlPaiement) window.location.assign(commande.urlPaiement); }
}
