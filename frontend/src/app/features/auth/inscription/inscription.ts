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
    nom: ['', [Validators.required, Validators.pattern(/^[\p{L}\p{M}]+(?:[ '\u2019-][\p{L}\p{M}]+)*$/u)]],
    prenom: ['', [Validators.required, Validators.pattern(/^[\p{L}\p{M}]+(?:[ '\u2019-][\p{L}\p{M}]+)*$/u)]],
    email: ['', [Validators.required, Validators.email]],
    motDePasse: ['', [Validators.required, Validators.minLength(6)]],
    telephone: ['', Validators.pattern(/^(?:[0-9]{9})?$/)],
    adresse: ['']
  });

  filtrerNom(champ: 'nom' | 'prenom', event: Event): void {
    const input = event.target as HTMLInputElement;
    const valeur = input.value.replace(/[0-9]/g, '');
    if (valeur !== input.value) {
      input.value = valeur;
      this.formulaire.controls[champ].setValue(valeur);
    }
  }

  filtrerTelephone(event: Event): void {
    const input = event.target as HTMLInputElement;
    const valeur = input.value.replace(/\D/g, '').slice(0, 9);
    if (valeur !== input.value) {
      input.value = valeur;
      this.formulaire.controls.telephone.setValue(valeur);
    }
  }

  soumettre(): void {
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }
    this.store.dispatch(AuthActions.inscrire({ request: this.formulaire.getRawValue() }));
  }
}
