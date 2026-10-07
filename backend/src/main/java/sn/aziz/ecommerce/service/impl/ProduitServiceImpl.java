package sn.aziz.ecommerce.service.impl;

import sn.aziz.ecommerce.dto.ProduitDTO;
import sn.aziz.ecommerce.entity.Categorie;
import sn.aziz.ecommerce.entity.Produit;
import sn.aziz.ecommerce.exception.RessourceNonTrouveeException;
import sn.aziz.ecommerce.mapper.ProduitMapper;
import sn.aziz.ecommerce.repository.CategorieRepository;
import sn.aziz.ecommerce.repository.ProduitRepository;
import sn.aziz.ecommerce.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;
    private final ProduitMapper produitMapper;

    @Override
    public ProduitDTO creer(ProduitDTO dto) {
        Produit produit = produitMapper.toEntity(dto);
        if (dto.getCategorieId() != null) {
            Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                    .orElseThrow(() -> new RessourceNonTrouveeException("Catégorie introuvable"));
            produit.setCategorie(categorie);
        }
        return produitMapper.toDTO(produitRepository.save(produit));
    }

    @Override
    public ProduitDTO obtenirParId(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Produit introuvable avec l'id : " + id));
        return produitMapper.toDTO(produit);
    }

    @Override
    public Page<ProduitDTO> obtenirTous(Pageable pageable) {
        return produitRepository.findAll(pageable).map(produitMapper::toDTO);
    }

    @Override
    public Page<ProduitDTO> obtenirParCategorie(Long categorieId, Pageable pageable) {
        return produitRepository.findByCategorieId(categorieId, pageable).map(produitMapper::toDTO);
    }

    @Override
    public Page<ProduitDTO> rechercherParNom(String nom, Pageable pageable) {
        return produitRepository.findByNomContainingIgnoreCase(nom, pageable).map(produitMapper::toDTO);
    }

    @Override
    public ProduitDTO modifier(Long id, ProduitDTO dto) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Produit introuvable avec l'id : " + id));

        produit.setNom(dto.getNom());
        produit.setDescription(dto.getDescription());
        produit.setPrix(dto.getPrix());
        produit.setQuantiteStock(dto.getQuantiteStock());
        produit.setImageUrl(dto.getImageUrl());

        if (dto.getCategorieId() != null) {
            Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                    .orElseThrow(() -> new RessourceNonTrouveeException("Catégorie introuvable"));
            produit.setCategorie(categorie);
        }

        return produitMapper.toDTO(produitRepository.save(produit));
    }

    @Override
    public void supprimer(Long id) {
        if (!produitRepository.findById(id).isPresent()) {
            throw new RessourceNonTrouveeException("Produit introuvable avec l'id : " + id);
        }
        produitRepository.deleteById(id);
    }
}