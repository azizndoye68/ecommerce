import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AsyncPipe } from '@angular/common';
import { Store } from '@ngrx/store';
import { AuthActions } from '../../store/auth/auth.actions';
import { selectEstConnecte, selectUtilisateur } from '../../store/auth/auth.selectors';
import { PanierService } from '../../core/services/panier.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, AsyncPipe],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  private readonly store = inject(Store);
  readonly panierService = inject(PanierService);
  readonly estConnecte$ = this.store.select(selectEstConnecte);
  readonly utilisateur$ = this.store.select(selectUtilisateur);
  readonly nombreArticles = computed(() => this.panierService.panier()?.lignes.reduce((total, ligne) => total + ligne.quantite, 0) ?? 0);
  deconnecter() { this.store.dispatch(AuthActions.deconnecter()); }
}
