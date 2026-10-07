import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { AsyncPipe } from '@angular/common';
import { AuthActions } from '../../../store/auth/auth.actions';
import { selectChargement, selectErreur } from '../../../store/auth/auth.selectors';

@Component({
  selector: 'app-inscription',
  imports: [ReactiveFormsModule, RouterLink, AsyncPipe],
  templateUrl: './inscription.html',
  styleUrl: './inscription.css'
})
export class Inscription {
  private fb = inject(FormBuilder);
  private store = inject(Store);

  chargement$ = this.store.select(selectChargement);
  erreur$ = this.store.select(selectErreur);

  formulaire = this.fb.nonNullable.group({
    nom: ['', Validators.required],
    prenom: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    motDePasse: ['', [Validators.required, Validators.minLength(12)]],
    telephone: [''],
    adresse: ['']
  });

  soumettre(): void {
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }
    this.store.dispatch(AuthActions.inscrire({ request: this.formulaire.getRawValue() }));
  }
}
