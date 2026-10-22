import { Component, DestroyRef, OnInit, computed, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AsyncPipe } from '@angular/common';
import { Store } from '@ngrx/store';
import { AuthActions } from '../../store/auth/auth.actions';
import { selectEstConnecte, selectUtilisateur } from '../../store/auth/auth.selectors';
import { PanierService } from '../../core/services/panier.service';
import { catchError, distinctUntilChanged, of, switchMap, tap } from 'rxjs';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, AsyncPipe],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private readonly store = inject(Store);
  readonly panierService = inject(PanierService);
  readonly estConnecte$ = this.store.select(selectEstConnecte);
  readonly utilisateur$ = this.store.select(selectUtilisateur);
  readonly nombreArticles = computed(() => this.panierService.panier()?.lignes.reduce((total, ligne) => total + ligne.quantite, 0) ?? 0);
  ngOnInit() {
    this.estConnecte$.pipe(
      distinctUntilChanged(),
      tap((estConnecte) => { if (!estConnecte) this.panierService.reinitialiser(); }),
      switchMap((estConnecte) => estConnecte
        ? this.panierService.charger().pipe(catchError(() => of(null)))
        : of(null)),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }
  fermerMenu(menu: HTMLDetailsElement) { menu.open = false; }
  deconnecter(menu: HTMLDetailsElement) {
    this.fermerMenu(menu);
    this.store.dispatch(AuthActions.deconnecter());
  }
}
