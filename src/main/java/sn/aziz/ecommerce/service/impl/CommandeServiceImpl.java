package sn.aziz.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.aziz.ecommerce.dto.*;
import sn.aziz.ecommerce.entity.*;
import sn.aziz.ecommerce.exception.RessourceNonTrouveeException;
import sn.aziz.ecommerce.repository.*;
import sn.aziz.ecommerce.service.CommandeService;
import sn.aziz.ecommerce.service.WaveService;
import sn.aziz.ecommerce.service.OrangeMoneyService;
import sn.aziz.ecommerce.service.PaiementInitialisation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeServiceImpl implements CommandeService {
    private final CommandeRepository commandeRepository;
    private final PanierRepository panierRepository;
    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final WaveService waveService;
    private final OrangeMoneyService orangeMoneyService;

    @Override
    @Transactional
    public CommandeDTO creer(String email, CreerCommandeRequest request) {
        Utilisateur utilisateur = utilisateur(email);
        Panier panier = panierRepository.findByUtilisateurId(utilisateur.getId())
                .orElseThrow(() -> new IllegalArgumentException("Votre panier est vide"));
        if (panier.getLignes().isEmpty()) throw new IllegalArgumentException("Votre panier est vide");

        Commande commande = new Commande();
        commande.setUtilisateur(utilisateur);
        commande.setModePaiement(request.getModePaiement());
        commande.setStatut(StatutCommande.EN_ATTENTE_PAIEMENT);
        BigDecimal total = BigDecimal.ZERO;
        for (LignePanier lignePanier : new ArrayList<>(panier.getLignes())) {
            Produit produit = produitRepository.findWithLockById(lignePanier.getProduit().getId())
                    .orElseThrow(() -> new RessourceNonTrouveeException("Produit introuvable"));
            if (produit.getQuantiteStock() < lignePanier.getQuantite())
                throw new IllegalArgumentException("Stock insuffisant pour le produit : " + produit.getNom());
            produit.setQuantiteStock(produit.getQuantiteStock() - lignePanier.getQuantite());
            LigneCommande ligne = new LigneCommande();
            ligne.setProduitId(produit.getId()); ligne.setNomProduit(produit.getNom());
            ligne.setPrixUnitaire(produit.getPrix()); ligne.setQuantite(lignePanier.getQuantite());
            commande.ajouterLigne(ligne);
            total = total.add(produit.getPrix().multiply(BigDecimal.valueOf(lignePanier.getQuantite())));
        }
        commande.setTotal(total);
        commande = commandeRepository.saveAndFlush(commande);
        PaiementInitialisation paiement = initialiserPaiement(commande);
        commande.setReferencePaiement(paiement.reference()); commande.setUrlPaiement(paiement.urlPaiement());
        panier.getLignes().clear();
        return toDTO(commandeRepository.save(commande));
    }

    @Override
    @Transactional
    public CommandeDTO confirmerPaiement(String email, Long commandeId) {
        Commande commande = commandeUtilisateur(email, commandeId);
        if (commande.getModePaiement() == ModePaiement.ORANGE_MONEY)
            throw new IllegalArgumentException("La confirmation Orange Money doit provenir du webhook marchand sécurisé");
        return confirmer(commande);
    }

    private CommandeDTO confirmer(Commande commande) {
        if (commande.getStatut() == StatutCommande.PAYEE) return toDTO(commande);
        if (commande.getStatut() != StatutCommande.EN_ATTENTE_PAIEMENT) throw new IllegalArgumentException("Cette commande ne peut plus être payée");
        if (commande.getModePaiement() == ModePaiement.WAVE && !waveService.estPaye(commande.getReferencePaiement()))
            throw new IllegalArgumentException("Le paiement Wave n'est pas encore confirmé");
        commande.setStatut(StatutCommande.PAYEE);
        commande.setUrlPaiement(null);
        return toDTO(commandeRepository.save(commande));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeDTO> mesCommandes(String email) {
        return commandeRepository.findByUtilisateurIdOrderByCreeLeDesc(utilisateur(email).getId()).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeDTO obtenir(String email, Long commandeId) { return toDTO(commandeUtilisateur(email, commandeId)); }

    private Utilisateur utilisateur(String email) {
        return utilisateurRepository.findByEmail(email).orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable"));
    }
    private Commande commandeUtilisateur(String email, Long id) {
        return commandeRepository.findByIdAndUtilisateurId(id, utilisateur(email).getId())
                .orElseThrow(() -> new RessourceNonTrouveeException("Commande introuvable"));
    }
    private CommandeDTO toDTO(Commande commande) {
        List<LigneCommandeDTO> lignes = commande.getLignes().stream().map(l -> LigneCommandeDTO.builder()
                .produitId(l.getProduitId()).nomProduit(l.getNomProduit()).prixUnitaire(l.getPrixUnitaire())
                .quantite(l.getQuantite()).sousTotal(l.getPrixUnitaire().multiply(BigDecimal.valueOf(l.getQuantite()))).build()).toList();
        return CommandeDTO.builder().id(commande.getId()).statut(commande.getStatut()).modePaiement(commande.getModePaiement())
                .total(commande.getTotal()).lignes(lignes).referencePaiement(commande.getReferencePaiement())
                .urlPaiement(commande.getUrlPaiement()).creeLe(commande.getCreeLe()).build();
    }

    private PaiementInitialisation initialiserPaiement(Commande commande) {
        return switch (commande.getModePaiement()) {
            case WAVE -> waveService.creerSession(commande.getId(), commande.getTotal());
            case ORANGE_MONEY -> orangeMoneyService.initialiser(commande.getId(), commande.getTotal());
        };
    }
}
