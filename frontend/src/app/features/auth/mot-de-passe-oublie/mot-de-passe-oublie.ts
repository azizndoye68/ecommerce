import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-mot-de-passe-oublie',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './mot-de-passe-oublie.html',
  styleUrl: './mot-de-passe-oublie.css',
})
export class MotDePasseOublie {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  readonly envoiEnCours = signal(false);
  readonly erreur = signal('');
  readonly demandeEnvoyee = signal(false);
  readonly formulaire = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
  });

  demanderLien() {
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }
    this.erreur.set('');
    this.envoiEnCours.set(true);
    this.auth.demanderReinitialisation(this.formulaire.getRawValue().email).subscribe({
      next: () => {
        this.demandeEnvoyee.set(true);
        this.envoiEnCours.set(false);
      },
      error: (e) => {
        this.erreur.set(e.error?.message ?? "L'envoi de l'e-mail a échoué. Réessaie plus tard.");
        this.envoiEnCours.set(false);
      },
    });
  }
}
