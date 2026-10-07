export interface LignePanier {
  produitId: number;
  nom: string;
  imageUrl?: string;
  prixUnitaire: number;
  quantite: number;
  sousTotal: number;
}

export interface Panier {
  id: number;
  lignes: LignePanier[];
  total: number;
}
