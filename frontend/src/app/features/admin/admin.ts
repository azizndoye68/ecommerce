import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { AdministrationService, CreerAdministrateurRequest } from '../../core/services/administration.service';
import { CatalogueService } from '../../core/services/catalogue.service';
import { Categorie, Produit } from '../../core/models/catalogue.model';
import { Commande, StatutCommande } from '../../core/models/commande.model';
import { Utilisateur } from '../../core/models/utilisateur.model';

type Onglet = 'produits' | 'categories' | 'commandes' | 'administrateurs';
type ProduitFormulaire = Omit<Produit, 'id' | 'categorieNom'>;

@Component({ selector: 'app-admin', imports: [FormsModule, CurrencyPipe, DatePipe], templateUrl: './admin.html', styleUrl: './admin.css' })
export class AdminPage implements OnInit {
  private readonly admin = inject(AdministrationService); private readonly catalogue = inject(CatalogueService);
  readonly produits = signal<Produit[]>([]); readonly categories = signal<Categorie[]>([]); readonly commandes = signal<Commande[]>([]); readonly administrateurs = signal<Utilisateur[]>([]); readonly erreur = signal(''); readonly succes = signal('');
  onglet: Onglet = 'produits'; readonly estSuperAdmin = localStorage.getItem('utilisateur')?.includes('SUPER_ADMIN') ?? false;
  editionProduitId?: number;
  produit: ProduitFormulaire = this.produitVide();
  categorie = { nom: '', description: '' };
  nouvelAdmin: CreerAdministrateurRequest = { nom: '', prenom: '', email: '', motDePasse: '', telephone: '', adresse: '' };
  readonly statuts: StatutCommande[] = ['PAYEE', 'EN_PREPARATION', 'EXPEDIEE', 'LIVREE', 'ANNULEE'];
  readonly televersementEnCours = signal(false);
  readonly apercuImage = signal('');

  ngOnInit() { this.chargerProduits(); this.chargerCategories(); this.chargerCommandes(); if (this.estSuperAdmin) this.chargerAdministrateurs(); }
  ouvrir(onglet: Onglet) { this.onglet = onglet; this.erreur.set(''); this.succes.set(''); }
  chargerProduits() { this.catalogue.produits(0).subscribe({ next: (p) => this.produits.set(p.content), error: () => this.erreur.set('Chargement des produits impossible.') }); }
  chargerCategories() { this.catalogue.categories().subscribe({ next: (c) => this.categories.set(c), error: () => this.erreur.set('Chargement des catégories impossible.') }); }
  chargerCommandes() { this.admin.commandes().subscribe({ next: (c) => this.commandes.set(c), error: (e) => this.erreur.set(e.error?.message ?? 'Chargement des commandes impossible.') }); }
  chargerAdministrateurs() { this.admin.administrateurs().subscribe({ next: (a) => this.administrateurs.set(a), error: (e) => this.erreur.set(e.error?.message ?? 'Chargement des administrateurs impossible.') }); }
  enregistrerProduit() {
    if (this.televersementEnCours()) return;
    if (!this.produit.nom || !this.produit.prix || this.produit.quantiteStock === null) { this.erreur.set('Nom, prix et stock sont obligatoires.'); return; }
    const operation = this.editionProduitId ? this.admin.modifierProduit(this.editionProduitId, this.produit) : this.admin.creerProduit(this.produit);
    operation.subscribe({ next: () => { this.notifier('Produit enregistré.'); this.produit = this.produitVide(); this.apercuImage.set(''); this.editionProduitId = undefined; this.chargerProduits(); }, error: (e) => this.erreur.set(e.error?.message ?? 'Enregistrement impossible.') });
  }
  editerProduit(produit: Produit) { this.editionProduitId = produit.id; this.produit = { nom: produit.nom, description: produit.description ?? '', prix: produit.prix, quantiteStock: produit.quantiteStock, imageUrl: produit.imageUrl ?? '', categorieId: produit.categorieId }; this.apercuImage.set(this.produit.imageUrl ?? ''); this.ouvrir('produits'); }
  selectionnerImage(event: Event) {
    const fichier = (event.target as HTMLInputElement).files?.[0];
    if (!fichier) return;
    if (!['image/png', 'image/jpeg', 'image/webp', 'image/gif'].includes(fichier.type)) {
      this.erreur.set('Choisissez une image PNG, JPEG, WebP ou GIF.');
      return;
    }
    if (fichier.size > 5 * 1024 * 1024) {
      this.erreur.set('L’image ne doit pas dépasser 5 Mo.');
      return;
    }
    this.erreur.set('');
    this.succes.set('');
    this.apercuImage.set(URL.createObjectURL(fichier));
    this.televersementEnCours.set(true);
    this.admin.televerserImage(fichier).subscribe({
      next: ({ imageUrl }) => {
        this.produit.imageUrl = imageUrl;
        this.apercuImage.set(imageUrl);
        this.televersementEnCours.set(false);
        this.notifier('Image téléversée. Enregistrez le produit pour appliquer la modification.');
      },
      error: (e) => {
        this.televersementEnCours.set(false);
        this.erreur.set(e.error?.message ?? 'Envoi de l’image impossible. Vérifiez la configuration Cloudinary.');
      }
    });
  }
  supprimerProduit(id: number) { if (!confirm('Supprimer ce produit ?')) return; this.admin.supprimerProduit(id).subscribe({ next: () => { this.notifier('Produit supprimé.'); this.chargerProduits(); }, error: (e) => this.erreur.set(e.error?.message ?? 'Suppression impossible.') }); }
  enregistrerCategorie() { if (!this.categorie.nom.trim()) return; this.admin.creerCategorie(this.categorie).subscribe({ next: () => { this.notifier('Catégorie créée.'); this.categorie = { nom: '', description: '' }; this.chargerCategories(); }, error: (e) => this.erreur.set(e.error?.message ?? 'Création impossible.') }); }
  supprimerCategorie(id: number) { if (!confirm('Supprimer cette catégorie ?')) return; this.admin.supprimerCategorie(id).subscribe({ next: () => { this.notifier('Catégorie supprimée.'); this.chargerCategories(); }, error: (e) => this.erreur.set(e.error?.message ?? 'Suppression impossible.') }); }
  changerStatut(commande: Commande, statut: string) { this.admin.changerStatutCommande(commande.id, statut as StatutCommande).subscribe({ next: () => { this.notifier('Statut mis à jour.'); this.chargerCommandes(); }, error: (e) => this.erreur.set(e.error?.message ?? 'Transition de statut refusée.') }); }
  creerAdministrateur() { if (this.nouvelAdmin.motDePasse.length < 12) { this.erreur.set('Le mot de passe doit comporter au moins 12 caractères.'); return; } this.admin.creerAdministrateur(this.nouvelAdmin).subscribe({ next: () => { this.notifier('Administrateur créé.'); this.nouvelAdmin = { nom: '', prenom: '', email: '', motDePasse: '', telephone: '', adresse: '' }; this.chargerAdministrateurs(); }, error: (e) => this.erreur.set(e.error?.message ?? 'Création impossible.') }); }
  changerStatutAdmin(admin: Utilisateur, actif: boolean) { if (!admin.id) return; this.admin.changerStatutAdministrateur(admin.id, actif).subscribe({ next: () => { this.notifier('Statut mis à jour.'); this.chargerAdministrateurs(); }, error: (e) => this.erreur.set(e.error?.message ?? 'Mise à jour impossible.') }); }
  private produitVide(): ProduitFormulaire { return { nom: '', description: '', prix: 0, quantiteStock: 0, imageUrl: '', categorieId: undefined }; }
  private notifier(message: string) { this.erreur.set(''); this.succes.set(message); }
}
