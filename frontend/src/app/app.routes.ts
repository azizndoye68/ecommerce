import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./features/catalogue/catalogue').then((m) => m.Catalogue) },
  { path: 'produits/:id', loadComponent: () => import('./features/produit-detail/produit-detail').then((m) => m.ProduitDetail) },
  { path: 'connexion', loadComponent: () => import('./features/auth/connexion/connexion').then((m) => m.Connexion) },
  { path: 'inscription', loadComponent: () => import('./features/auth/inscription/inscription').then((m) => m.Inscription) },
  { path: 'panier', canActivate: [authGuard], loadComponent: () => import('./features/panier/panier').then((m) => m.PanierPage) },
  { path: 'commande', canActivate: [authGuard], loadComponent: () => import('./features/checkout/checkout').then((m) => m.Checkout) },
  { path: 'commandes', canActivate: [authGuard], loadComponent: () => import('./features/commandes/commandes').then((m) => m.Commandes) },
  { path: 'profil', canActivate: [authGuard], loadComponent: () => import('./features/profil/profil').then((m) => m.Profil) },
  { path: 'admin', canActivate: [authGuard, roleGuard(['ADMIN', 'SUPER_ADMIN'])], loadComponent: () => import('./features/admin/admin').then((m) => m.AdminPage) },
  { path: '**', redirectTo: '' },
];
