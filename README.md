# SunuMarket — boutique en ligne

SunuMarket est une plateforme e-commerce composée d’un frontend Angular et d’une API Java Spring Boot. Elle permet de consulter un catalogue, créer un compte, gérer un panier, passer des commandes et suivre leur paiement et leur livraison. Les administrateurs gèrent les produits, les catégories, les commandes et les comptes administrateurs.

La boutique est hébergée avec le frontend sur Vercel et le backend sur Render. PostgreSQL conserve les données, Cloudinary héberge les photos de produits et UniTech Pay fournit les paiements Wave et Orange Money.

## Sommaire

- [Fonctionnalités](#fonctionnalités)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Lancer le projet en local](#lancer-le-projet-en-local)
- [Variables d’environnement](#variables-denvironnement)
- [Paiements et webhook UniTech Pay](#paiements-et-webhook-unitech-pay)
- [Images produits](#images-produits)
- [Déploiement](#déploiement)
- [API et documentation interactive](#api-et-documentation-interactive)
- [Commandes utiles](#commandes-utiles)
- [Dépannage](#dépannage)
- [Sécurité](#sécurité)

## Fonctionnalités

### Côté client

- Consultation du catalogue, recherche, filtres par catégorie et fiches détaillées.
- Création de compte, connexion, renouvellement de session et déconnexion.
- Panier associé au compte client.
- Commandes avec coordonnées de livraison et paiement Wave ou Orange Money via UniTech Pay.
- Confirmation automatique du paiement à partir du webhook UniTech Pay.
- Suivi des étapes : **Payée → En préparation → Expédiée → Livrée**.
- Modification des informations du profil et du mot de passe.
- Réinitialisation du mot de passe par e-mail.
- Suppression du compte client.

### Côté administration

- Création, modification et suppression de produits et de catégories.
- Téléversement de photos produits vers Cloudinary.
- Liste des commandes, filtres par statut et progression de leur préparation/livraison.
- Gestion des comptes administrateurs par le Super Admin.
- Protection contre la désactivation du dernier Super Admin et contre sa propre désactivation.

## Architecture

```text
frontend/  Angular, TypeScript, NgRx, styles CSS
backend/   Java 21, Spring Boot, Spring Security, JPA, Flyway
           PostgreSQL, UniTech Pay, Cloudinary, SMTP
```

Le frontend appelle l’API à l’adresse définie dans `frontend/src/app/environments/environment.ts` en production et dans `environment.development.ts` en développement. Les clés privées restent côté backend.

Les migrations SQL se trouvent dans `backend/src/main/resources/db/migration` et sont appliquées par Flyway au démarrage.

## Prérequis

- Git
- Java 21
- PostgreSQL (installation locale ou instance accessible)
- Node.js compatible avec Angular 21 et npm
- Un compte Cloudinary pour les téléversements d’images
- Un compte UniTech Pay pour les paiements réels

Pour le développement local, Maven est inclus avec le wrapper `backend/mvnw` / `backend/mvnw.cmd`.

## Lancer le projet en local

### 1. Préparer PostgreSQL

Crée une base et un utilisateur PostgreSQL pour le développement, par exemple `ecommerce_db`. La première connexion de Spring/Flyway crée le schéma à partir des migrations.

### 2. Préparer le backend

Dans PowerShell :

```powershell
cd C:\Projets\ecommerce\backend
Copy-Item .env.example .env
```

Ouvre `backend/.env` et renseigne au minimum les valeurs locales suivantes :

```ini
DB_URL=jdbc:postgresql://localhost:5432/ecommerce_db
DB_USERNAME=postgres
DB_PASSWORD=TON_MOT_DE_PASSE_POSTGRES
JWT_SECRET=UNE_CLE_ALEATOIRE_D_AU_MOINS_32_CARACTERES
ADMIN_EMAIL=admin@ecommerce.com
ADMIN_PASSWORD=UN_MOT_DE_PASSE_LOCAL_FORT
PAIEMENT_SIMULATION_ACTIVE=true
```

Laisse les identifiants de service non utilisés vides en local. Pour envoyer des images vers Cloudinary depuis le backend local, remplis aussi les trois variables `CLOUDINARY_*`. Pour les e-mails de réinitialisation, configure les variables SMTP.

Démarre l’API depuis le dossier `backend` :

```powershell
.\mvnw.cmd spring-boot:run
```

L’API locale écoute normalement sur `http://localhost:8081`.

### 3. Préparer le frontend

Dans un autre terminal :

```powershell
cd C:\Projets\ecommerce\frontend
npm ci
npm start
```

Ouvre ensuite [http://localhost:4200](http://localhost:4200). Le frontend de développement utilise l’API `http://localhost:8081/api` et affiche l’option de paiement simulé. Le backend doit également avoir `PAIEMENT_SIMULATION_ACTIVE=true` pour accepter ce mode.

Le paiement simulé est réservé au développement : il ne traite aucun paiement réel et doit rester désactivé en production.

## Variables d’environnement

Le modèle complet est dans [`backend/.env.example`](backend/.env.example). Copie-le vers `backend/.env` pour le développement. Ce fichier local est ignoré par Git. Sur Render, configure les valeurs dans **Dashboard → service backend → Environment**.

### Base de données et sécurité

| Variable | Rôle | Nécessaire |
|---|---|---|
| `DB_URL` | URL JDBC PostgreSQL (`jdbc:postgresql://hôte:5432/base`) | Oui |
| `DB_USERNAME` | Utilisateur PostgreSQL | Oui |
| `DB_PASSWORD` | Mot de passe PostgreSQL | Oui |
| `JWT_SECRET` | Clé de signature des jetons JWT ; utiliser une valeur aléatoire longue | Oui |
| `JWT_EXPIRATION` | Durée du jeton d’accès en millisecondes ; défaut : `900000` (15 min) | Non |
| `JWT_REFRESH_EXPIRATION` | Durée du jeton de renouvellement en millisecondes ; défaut : `2592000000` (30 jours) | Non |
| `CORS_ALLOWED_ORIGINS` | Origines frontend autorisées, séparées par des virgules | À définir en production |
| `JPA_DDL_AUTO` | Stratégie Hibernate ; conserver `validate` avec les migrations Flyway | Non |

### Compte Super Admin initial

| Variable | Rôle |
|---|---|
| `ADMIN_EMAIL` | Adresse du Super Admin initial |
| `ADMIN_PASSWORD` | Mot de passe initial ; le bootstrap est ignoré si la valeur est vide |
| `ADMIN_NOM` | Nom affiché ; valeur par défaut `Admin` |
| `ADMIN_PRENOM` | Prénom affiché ; valeur par défaut `Super` |

Au démarrage, l’application crée le Super Admin initial uniquement si `ADMIN_PASSWORD` est renseigné et qu’aucun Super Admin n’existe encore. Ces variables ne remplacent pas le compte lorsque la base contient déjà un Super Admin.

### Cloudinary — photos de produits

| Variable | Où trouver la valeur |
|---|---|
| `CLOUDINARY_CLOUD_NAME` | Dashboard Cloudinary → Product Environment → Cloud name |
| `CLOUDINARY_API_KEY` | Cloudinary → Settings → API Keys |
| `CLOUDINARY_API_SECRET` | Cloudinary → Settings → API Keys → afficher le secret |

Ces trois valeurs doivent provenir du même environnement Cloudinary. Le secret API doit être configuré uniquement sur le backend, jamais dans Angular, Vercel ou GitHub. Les fichiers envoyés sont limités à 5 Mo et les formats acceptés sont PNG, JPEG, WebP et GIF.

### UniTech Pay — Wave et Orange Money

| Variable | Rôle |
|---|---|
| `UNITECHPAY_BASE_URL` | API UniTech Pay ; défaut : `https://api.unitech.sn/api.php` |
| `UNITECHPAY_API_KEY` | Clé API UniTech Pay ; à conserver côté backend |
| `UNITECHPAY_ORANGE_ACTION` | `create_orange_om` ou `create_orange_maxit` |
| `UNITECHPAY_CALLBACK_SUCCESS` | URL HTTPS du frontend pour le retour après paiement réussi ; facultative |
| `UNITECHPAY_CALLBACK_CANCEL` | URL HTTPS du frontend pour le retour après annulation ; facultative |

L’application gère les retours navigateur séparément de la confirmation sécurisée reçue par webhook. Voir [Paiements et webhook UniTech Pay](#paiements-et-webhook-unitech-pay).

### E-mail et autres réglages

| Variable | Rôle |
|---|---|
| `MAIL_HOST`, `MAIL_PORT` | Serveur SMTP et port (Gmail : `smtp.gmail.com`, `587`) |
| `MAIL_USERNAME`, `MAIL_PASSWORD` | Identifiants SMTP ; avec Gmail, utiliser un mot de passe d’application |
| `MAIL_SMTP_AUTH`, `MAIL_SMTP_STARTTLS` | Authentification et TLS SMTP ; généralement `true` |
| `MAIL_FROM` | Adresse d’expédition affichée, facultative selon le fournisseur |
| `FRONTEND_URL` | Origine du frontend utilisée dans le lien de réinitialisation du mot de passe |
| `PAIEMENT_SIMULATION_ACTIVE` | Autorise le paiement fictif ; `true` uniquement en développement local |
| `AUTH_RATE_LIMIT`, `AUTH_RATE_LIMIT_WINDOW` | Limite des tentatives d’authentification |
| `SESSION_CLEANUP_MS` | Intervalle de nettoyage des sessions expirées |

Le frontend utilise actuellement l’URL de production du backend configurée dans son fichier d’environnement. Les secrets du backend ne sont pas des variables Vercel.

## Paiements et webhook UniTech Pay

UniTech Pay crée les paiements Wave et Orange Money. Après le paiement, le navigateur retourne vers la boutique, mais ce retour seul ne confirme pas la commande. UniTech doit envoyer une notification serveur-à-serveur au webhook de la boutique pour passer la commande à **Payée**.

Configurer dans le tableau de bord UniTech Pay l’URL de webhook suivante et activer l’événement de paiement réussi :

```text
https://ecommerce-kzhi.onrender.com/api/webhooks/unitechpay
```

Ne pas utiliser `https://api.unitech.sn/webhook.php?...` comme URL webhook de la boutique : cette adresse appartient au domaine UniTech Pay.

Les variables `UNITECHPAY_CALLBACK_SUCCESS` et `UNITECHPAY_CALLBACK_CANCEL` sont les retours du navigateur, pas le webhook. Elles peuvent être laissées vides si UniTech Pay n’exige pas de les envoyer dans la demande de paiement.

Pour un test depuis un backend local, UniTech ne peut pas joindre `localhost`. Il faut exposer temporairement le port local via un tunnel HTTPS, par exemple `ngrok http 8081`, puis déclarer l’URL temporaire terminée par `/api/webhooks/unitechpay` dans UniTech Pay. Garder le backend et le tunnel en marche pendant l’essai. Une commande de la base locale doit être confirmée par le backend local.

En cas de paiement débité mais de commande encore en attente, ne pas payer une seconde fois. Vérifier la réception du webhook dans les logs Render et demander à UniTech Pay de renvoyer la notification si nécessaire.

## Images produits

Dans le formulaire d’administration, le fichier choisi depuis l’ordinateur est d’abord affiché en aperçu local. Pour qu’il apparaisse durablement dans le catalogue, il doit ensuite être téléversé depuis le navigateur vers le backend, puis stocké sur Cloudinary. L’URL Cloudinary retournée est enregistrée dans le produit.

Si l’aperçu apparaît mais pas l’image du catalogue :

1. Vérifier que le backend est déployé et que les trois variables `CLOUDINARY_*` sont présentes dans l’environnement Render.
2. Réessayer le téléversement et vérifier les logs Render si Cloudinary le refuse.
3. Enregistrer le produit après le téléversement réussi.

## Déploiement

Le dépôt contient les deux applications. Déployer le backend et le frontend séparément.

### Backend sur Render

1. Créer ou ouvrir le service Web Render relié au dépôt GitHub.
2. Définir **Root Directory** à `backend`.
3. Utiliser le Dockerfile `backend/Dockerfile` (à partir de la racine `backend`). Le Dockerfile construit le projet Java 21 et démarre Spring Boot sur le port fourni par Render.
4. Créer ou sélectionner une base PostgreSQL Render. Utiliser les informations de connexion **internes** à Render pour le service backend et convertir l’adresse en URL JDBC :

   ```text
   jdbc:postgresql://HOTE_INTERNE:5432/NOM_DE_LA_BASE
   ```

5. Configurer dans l’environnement Render les variables database, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS` et `UNITECHPAY_API_KEY`. Ajouter également les variables Cloudinary et SMTP si ces services sont utilisés.
6. Mettre `CORS_ALLOWED_ORIGINS` à l’origine Vercel exacte, sans chemin final, par exemple `https://nom-du-site.vercel.app`.
7. Déployer. Render exécute les migrations Flyway au démarrage.
8. Déclarer dans UniTech Pay le webhook de production indiqué plus haut.

Ne pas utiliser `PAIEMENT_SIMULATION_ACTIVE=true` en production.

### Frontend sur Vercel

1. Créer ou ouvrir le projet Vercel relié au même dépôt GitHub.
2. Définir **Root Directory** à `frontend`.
3. Framework : Angular (ou configuration équivalente si Vercel ne le détecte pas).
4. Commande d’installation : `npm ci`.
5. Commande de build : `npm run build`.
6. Répertoire de sortie Angular : `dist/frontend/browser`.
7. Déployer. L’URL de l’API de production est définie dans `frontend/src/app/environments/environment.ts`.

Lorsqu’un changement est poussé sur la branche configurée dans les paramètres Vercel/Render, les plateformes lancent normalement un nouveau déploiement. Vérifier les journaux de build si le déploiement échoue. Les routes Angular doivent également retomber sur `index.html` pour permettre l’ouverture directe d’une page comme `/commandes` ou `/admin`.

### Après un changement

- Les changements `frontend/` nécessitent un build et un déploiement Vercel.
- Les changements `backend/` nécessitent un build et un déploiement Render.
- Un changement de variable d’environnement nécessite un redémarrage ou un nouveau déploiement du service concerné.
- Les variables Cloudinary, UniTech Pay, JWT, SMTP et base de données restent uniquement sur Render / dans le `.env` local ignoré par Git.

## API et documentation interactive

Base locale : `http://localhost:8081/api`  
Base production : `https://ecommerce-kzhi.onrender.com/api`

Principales routes :

| Domaine | Routes principales | Accès |
|---|---|---|
| Authentification | `/auth/inscription`, `/auth/connexion`, `/auth/rafraichir`, `/auth/deconnexion` | Public/authentifié selon l’action |
| Mot de passe | `/auth/mot-de-passe-oublie`, `/auth/reinitialiser-mot-de-passe` | Public |
| Catalogue | `/produits`, `/produits/{id}`, `/produits/recherche`, `/categories` | Lecture publique |
| Images | `POST /produits/image` | Admin |
| Panier | `/panier`, `/panier/lignes` | Client authentifié |
| Commandes client | `/commandes` | Client authentifié |
| Commandes admin | `/admin/commandes` | Admin |
| Gestion admins | `/admin/administrateurs` | Super Admin |
| Profil | `/utilisateurs/moi` | Utilisateur authentifié |
| Paiement | `POST /webhooks/unitechpay` | Notification UniTech Pay signée |

La documentation Swagger/OpenAPI est disponible dans le backend sur `/swagger-ui/index.html` (par exemple `http://localhost:8081/swagger-ui/index.html` en local).

## Commandes utiles

Depuis `backend/` :

```powershell
.\mvnw.cmd spring-boot:run      # lancer l'API
.\mvnw.cmd -DskipTests package # compiler et empaqueter sans lancer les tests
.\mvnw.cmd test               # lancer les tests backend
```

Depuis `frontend/` :

```powershell
npm start          # serveur de développement Angular
npm run build      # build de production
npm test           # tests frontend
```

## Dépannage

| Symptôme | Vérifications |
|---|---|
| Le backend ne démarre pas | Vérifier Java 21, PostgreSQL, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` et `JWT_SECRET`. |
| Le catalogue ne charge pas | Vérifier que Render indique le service comme Live, l’URL API du frontend et les origines CORS. |
| Le panier ou les commandes renvoient 401/403 | Se reconnecter, vérifier le jeton et le rôle du compte, puis recharger la page. |
| Paiement débité, commande en attente | Ne pas refaire le paiement. Vérifier le webhook UniTech Pay et demander un renvoi de notification si nécessaire. |
| Téléversement d’image indisponible | Vérifier les trois variables Cloudinary sur Render, les logs du backend, le type de fichier et la limite de 5 Mo. |
| L’e-mail de réinitialisation n’arrive pas | Vérifier les variables SMTP, le mot de passe d’application, `MAIL_FROM` et `FRONTEND_URL`. |
| Page directe Angular renvoie 404 | Vérifier le réglage de fallback SPA de Vercel vers `index.html`. |
| Le déploiement ne se lance pas | Vérifier la branche de production choisie sur la plateforme et les logs de déploiement/build. |

## Sécurité

- Ne jamais committer `backend/.env`, les clés Cloudinary, la clé UniTech Pay, les secrets JWT ou les mots de passe.
- Ne pas mettre les secrets backend dans le frontend ni dans les variables d’environnement Vercel.
- Utiliser HTTPS pour les sites, callbacks et webhooks de production.
- Garder le paiement simulé désactivé en production.
- Utiliser les migrations Flyway ; éviter de modifier le schéma de production manuellement sans migration versionnée.

