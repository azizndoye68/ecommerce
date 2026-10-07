export interface Utilisateur {
  id?: number;
  email: string;
  nom: string;
  prenom: string;
  role: 'CLIENT' | 'ADMIN' | 'SUPER_ADMIN';
}
