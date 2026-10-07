export interface InscriptionRequest {
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  telephone?: string;
  adresse?: string;
}

export interface ConnexionRequest {
  email: string;
  motDePasse: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  email: string;
  nom: string;
  prenom: string;
  role: 'CLIENT' | 'ADMIN' | 'SUPER_ADMIN';
}

export interface RefreshTokenRequest {
  refreshToken: string;
}