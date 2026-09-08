package sn.aziz.ecommerce.service;

import sn.aziz.ecommerce.dto.ProduitDTO;

import java.util.List;

public interface ProduitService {

    ProduitDTO creer(ProduitDTO produitDTO);

    ProduitDTO obtenirParId(Long id);

    List<ProduitDTO> obtenirTous();

    List<ProduitDTO> obtenirParCategorie(Long categorieId);

    List<ProduitDTO> rechercherParNom(String nom);

    ProduitDTO modifier(Long id, ProduitDTO produitDTO);

    void supprimer(Long id);
}