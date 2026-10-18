import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { ProfilService } from '../../core/services/profil.service';
import { Utilisateur } from '../../core/models/utilisateur.model';
import { AuthActions } from '../../store/auth/auth.actions';

@Component({
  selector: 'app-profil',
  imports: [ReactiveFormsModule],
  templateUrl: './profil.html',
  styleUrl: './profil.css',
})
export class Profil implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly store = inject(Store);
  private readonly profilService = inject(ProfilService);
  readonly chargement = signal(true);
  readonly enregistrement = signal(false);
  readonly changementMotDePasse = signal(false);
  readonly erreur = signal('');
  readonly succes = signal('');
  readonly profil = signal<Utilisateur | null>(null);

  readonly informationsForm = this.fb.nonNullable.group({
    nom: ['', Validators.required],
    prenom: ['', Validators.required],
    telephone: [''],
    adresse: [''],
  });
  readonly motDePasseForm = this.fb.nonNullable.group({
    motDePasseActuel: ['', Validators.required],
    nouveauMotDePasse: ['', [Validators.required, Validators.minLength(6)]],
    confirmation: ['', Validators.required],
  });

  ngOnInit() {
    this.profilService.obtenir().subscribe({
      next: (profil) => {
        this.profil.set(profil);
        this.informationsForm.patchValue({ nom: profil.nom, prenom: profil.prenom, telephone: profil.telephone ?? '', adresse: profil.adresse ?? '' });
        this.chargement.set(false);
      },
      error: (e) => {
        this.erreur.set(e.error?.message ?? 'Impossible de charger le profil.');
        this.chargement.set(false);
      },
    });
  }

  enregistrerInformations() {
    if (this.informationsForm.invalid) { this.informationsForm.markAllAsTouched(); return; }
    this.erreur.set(''); this.succes.set(''); this.enregistrement.set(true);
    this.profilService.modifier(this.informationsForm.getRawValue()).subscribe({
      next: (profil) => {
        this.profil.set(profil);
        this.store.dispatch(AuthActions.utilisateurMisAJour({ utilisateur: profil }));
        this.succes.set('Tes informations personnelles ont été mises à jour.');
        this.enregistrement.set(false);
      },
      error: (e) => { this.erreur.set(e.error?.message ?? 'Impossible de mettre à jour les informations.'); this.enregistrement.set(false); },
    });
  }

  changerMotDePasse() {
    if (this.motDePasseForm.invalid) { this.motDePasseForm.markAllAsTouched(); return; }
    const { motDePasseActuel, nouveauMotDePasse, confirmation } = this.motDePasseForm.getRawValue();
    if (nouveauMotDePasse !== confirmation) { this.erreur.set('La confirmation du nouveau mot de passe ne correspond pas.'); return; }
    this.erreur.set(''); this.succes.set(''); this.changementMotDePasse.set(true);
    this.profilService.changerMotDePasse(motDePasseActuel, nouveauMotDePasse).subscribe({
      next: () => {
        this.motDePasseForm.reset();
        this.succes.set('Ton mot de passe a été changé.');
        this.changementMotDePasse.set(false);
      },
      error: (e) => { this.erreur.set(e.error?.message ?? 'Impossible de changer le mot de passe.'); this.changementMotDePasse.set(false); },
    });
  }
}
