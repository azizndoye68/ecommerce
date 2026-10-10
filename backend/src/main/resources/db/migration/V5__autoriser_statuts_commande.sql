-- Les états de commande doivent suivre l'énumération StatutCommande.
ALTER TABLE commandes DROP CONSTRAINT IF EXISTS commandes_statut_check;
ALTER TABLE commandes
    ADD CONSTRAINT commandes_statut_check
    CHECK (statut IN (
        'EN_ATTENTE_PAIEMENT',
        'PAYEE',
        'EN_PREPARATION',
        'EXPEDIEE',
        'LIVREE',
        'ANNULEE'
    ));
