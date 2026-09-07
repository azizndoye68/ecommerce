package sn.aziz.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.aziz.ecommerce.dto.*;
import sn.aziz.ecommerce.entity.*;
import sn.aziz.ecommerce.exception.RessourceNonTrouveeException;
import sn.aziz.ecommerce.repository.PanierRepository;
import sn.aziz.ecommerce.repository.ProduitRepository;
import sn.aziz.ecommerce.repository.UtilisateurRepository;
import sn.aziz.ecommerce.service.PanierService;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PanierServiceImpl implements PanierService {
    private final PanierRepository panierRepository;
    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    public PanierDTO obtenirPanierConnecte(String email) { return toDTO(trouverOuCreer(email)); }

    @Override
    public PanierDTO ajouter(String email, AjouterAuPanierRequest request) {
        Panier panier = trouverOuCreer(email);
        Produit produit = produitRepository.findById(request.getProduitId())
                .orElseThrow(() -> new RessourceNonTrouveeException("Produit introuvable"));
        LignePanier ligne = panier.getLignes().stream()
                .filter(l -> l.getProduit().getId().equals(produit.getId())).findFirst().orElse(null);
        int nouvelleQuantite = request.getQuantite() + (ligne == null ? 0 : ligne.getQuantite());
        verifierStock(produit, nouvelleQuantite);
        if (ligne == null) {
            ligne = new LignePanier(); ligne.setProduit(produit); ligne.setQuantite(nouvelleQuantite); panier.ajouterLigne(ligne);
        } else ligne.setQuantite(nouvelleQuantite);
        return toDTO(panierRepository.save(panier));
    }

    @Override
    public PanierDTO modifierQuantite(String email, Long produitId, ModifierQuantitePanierRequest request) {
        Panier panier = trouverOuCreer(email);
        LignePanier ligne = trouverLigne(panier, produitId);
        verifierStock(ligne.getProduit(), request.getQuantite());
        ligne.setQuantite(request.getQuantite());
        return toDTO(panierRepository.save(panier));
    }

    @Override
    public void supprimerLigne(String email, Long produitId) {
        Panier panier = trouverOuCreer(email);
        panier.retirerLigne(trouverLigne(panier, produitId));
        panierRepository.save(panier);
    }

    @Override
    public void vider(String email) {
        Panier panier = trouverOuCreer(email);
        panier.getLignes().clear();
        panierRepository.save(panier);
    }

    private Panier trouverOuCreer(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable"));
        return panierRepository.findByUtilisateurId(utilisateur.getId()).orElseGet(() -> {
            Panier panier = new Panier(); panier.setUtilisateur(utilisateur); return panierRepository.save(panier);
        });
    }
    private LignePanier trouverLigne(Panier panier, Long produitId) {
        return panier.getLignes().stream().filter(l -> l.getProduit().getId().equals(produitId)).findFirst()
                .orElseThrow(() -> new RessourceNonTrouveeException("Produit absent du panier"));
    }
    private void verifierStock(Produit produit, int quantite) {
        if (produit.getQuantiteStock() < quantite) throw new IllegalArgumentException("Stock insuffisant pour le produit : " + produit.getNom());
    }
    public static PanierDTO toDTO(Panier panier) {
        List<LignePanierDTO> lignes = panier.getLignes().stream().map(l -> {
            BigDecimal sousTotal = l.getProduit().getPrix().multiply(BigDecimal.valueOf(l.getQuantite()));
            return LignePanierDTO.builder().produitId(l.getProduit().getId()).nom(l.getProduit().getNom())
                    .imageUrl(l.getProduit().getImageUrl()).prixUnitaire(l.getProduit().getPrix())
                    .quantite(l.getQuantite()).sousTotal(sousTotal).build();
        }).toList();
        BigDecimal total = lignes.stream().map(LignePanierDTO::getSousTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return PanierDTO.builder().id(panier.getId()).lignes(lignes).total(total).build();
    }
}
