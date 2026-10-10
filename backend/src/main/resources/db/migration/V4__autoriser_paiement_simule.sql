-- Le mode simulé est réservé au développement, mais doit être accepté par le schéma.
ALTER TABLE commandes DROP CONSTRAINT IF EXISTS commandes_mode_paiement_check;
ALTER TABLE commandes
    ADD CONSTRAINT commandes_mode_paiement_check
    CHECK (mode_paiement IN ('WAVE', 'ORANGE_MONEY', 'SIMULE'));
