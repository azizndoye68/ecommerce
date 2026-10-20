CREATE TABLE images_produits (
    id BIGINT NOT NULL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    type_contenu VARCHAR(50) NOT NULL,
    contenu BYTEA NOT NULL
);
