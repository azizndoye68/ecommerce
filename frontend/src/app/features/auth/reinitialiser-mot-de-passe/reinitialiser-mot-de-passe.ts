import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-reinitialiser-mot-de-passe',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './reinitialiser-mot-de-passe.html',
  styleUrl: './reinitialiser-mot-de-passe.css',
})
export class ReinitialiserMotDePasse {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  readonly token = inject(ActivatedRoute).snapshot.queryParamMap.get('token') ?? '';
  readonly envoiEnCours = signal(false);
  readonly termine = signal(false);
  readonly erreur = signal('');
  readonly formulaire = this.fb.nonNullable.group({
    nouveauMotDePasse: ['', [Validators.required, Validators.minLength(8)]],
    confirmation: ['', Validators.required],
  });

  reinitialiser() {
    if (!this.token) {
      this.erreur.set('Ce lien est incomplet. Demande un nouveau lien de réinitialisation.');
      return;
    }
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }
    const { nouveauMotDePasse, confirmation } = this.formulaire.getRawValue();
    if (nouveauMotDePasse !== confirmation) {
      this.erreur.set('Les deux mots de passe ne correspondent pas.');
      return;
    }
    this.erreur.set('');
    this.envoiEnCours.set(true);
    this.auth.reinitialiserMotDePasse(this.token, nouveauMotDePasse).subscribe({
      next: () => {
        this.termine.set(true);
        this.envoiEnCours.set(false);
      },
      error: (e) => {
        this.erreur.set(e.error?.message ?? 'Ce lien est invalide ou a expiré. Demande un nouveau lien.');
        this.envoiEnCours.set(false);
      },
    });
  }
}
