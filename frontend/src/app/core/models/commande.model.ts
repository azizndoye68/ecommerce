import { LignePanier } from './panier.model';

export type ModePaiement = 'WAVE' | 'ORANGE_MONEY' | 'SIMULE';
export type StatutCommande = 'EN_ATTENTE_PAIEMENT' | 'PAYEE' | 'EN_PREPARATION' | 'EXPEDIEE' | 'LIVREE' | 'ANNULEE';

export interface AdresseLivraison {
  destinataire: string;
  telephone: string;
  adresse: string;
  ville: string;
  pays: string;
}

export interface CreerCommandeRequest {
  modePaiement: ModePaiement;
  adresseLivraison: AdresseLivraison;
}

export interface LigneCommande extends Omit<LignePanier, 'nom' | 'imageUrl'> {
  nomProduit: string;
}

export interface Commande {
  id: number;
  statut: StatutCommande;
  modePaiement: ModePaiement;
  total: number;
  lignes: LigneCommande[];
  referencePaiement?: string;
  urlPaiement?: string;
  destinataireLivraison: string;
  telephoneLivraison: string;
  adresseLivraison: string;
  villeLivraison: string;
  paysLivraison: string;
  creeLe: string;
}
