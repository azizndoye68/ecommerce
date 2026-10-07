import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { AsyncPipe } from '@angular/common';
import { AuthActions } from '../../../store/auth/auth.actions';
import { selectChargement, selectErreur } from '../../../store/auth/auth.selectors';

@Component({
  selector: 'app-connexion',
  imports: [ReactiveFormsModule, RouterLink, AsyncPipe],
  templateUrl: './connexion.html',
  styleUrl: './connexion.css'
})
export class Connexion {
  private fb = inject(FormBuilder);
  private store = inject(Store);

  chargement$ = this.store.select(selectChargement);
  erreur$ = this.store.select(selectErreur);

  formulaire = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    motDePasse: ['', Validators.required]
  });

  soumettre(): void {
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }
    this.store.dispatch(AuthActions.connecter({ request: this.formulaire.getRawValue() }));
  }
}