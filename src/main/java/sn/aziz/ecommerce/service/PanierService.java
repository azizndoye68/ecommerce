package sn.aziz.ecommerce.service;

import sn.aziz.ecommerce.dto.*;

public interface PanierService {
    PanierDTO obtenirPanierConnecte(String email);
    PanierDTO ajouter(String email, AjouterAuPanierRequest request);
    PanierDTO modifierQuantite(String email, Long produitId, ModifierQuantitePanierRequest request);
    void supprimerLigne(String email, Long produitId);
    void vider(String email);
}
