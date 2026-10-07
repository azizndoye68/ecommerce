package sn.aziz.ecommerce.service;

import sn.aziz.ecommerce.dto.*;
import java.util.List;
import sn.aziz.ecommerce.entity.ModePaiement;
import sn.aziz.ecommerce.entity.StatutCommande;

public interface CommandeService {
    CommandeDTO creer(String email, CreerCommandeRequest request);
    CommandeDTO confirmerPaiement(String email, Long commandeId);
    CommandeDTO confirmerPaiementMarchand(ModePaiement modePaiement, String referencePaiement);
    CommandeDTO modifierStatutLivraison(Long commandeId, StatutCommande statut);
    List<CommandeDTO> toutesLesCommandes();
    List<CommandeDTO> mesCommandes(String email);
    CommandeDTO obtenir(String email, Long commandeId);

    void annuler(String email, Long commandeId);
}
