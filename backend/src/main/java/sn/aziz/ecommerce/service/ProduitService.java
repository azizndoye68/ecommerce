package sn.aziz.ecommerce.service;

import sn.aziz.ecommerce.dto.ProduitDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProduitService {

    ProduitDTO creer(ProduitDTO produitDTO);

    ProduitDTO obtenirParId(Long id);

    Page<ProduitDTO> obtenirTous(Pageable pageable);

    Page<ProduitDTO> obtenirParCategorie(Long categorieId, Pageable pageable);

    Page<ProduitDTO> rechercherParNom(String nom, Pageable pageable);

    ProduitDTO modifier(Long id, ProduitDTO produitDTO);

    void supprimer(Long id);
}