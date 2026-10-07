export interface Categorie {
  id: number;
  nom: string;
  description?: string;
}

export interface Produit {
  id: number;
  nom: string;
  description?: string;
  prix: number;
  quantiteStock: number;
  imageUrl?: string;
  categorieId?: number;
  categorieNom?: string;
}

export interface PageResult<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
