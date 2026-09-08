-- Les schémas créés avant le rôle SUPER_ADMIN possèdent une contrainte Hibernate
-- limitant la colonne role à CLIENT et ADMIN.
ALTER TABLE utilisateurs DROP CONSTRAINT IF EXISTS utilisateurs_role_check;
ALTER TABLE utilisateurs
    ADD CONSTRAINT utilisateurs_role_check
    CHECK (role IN ('CLIENT', 'ADMIN', 'SUPER_ADMIN'));
