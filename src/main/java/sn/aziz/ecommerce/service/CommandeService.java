package sn.aziz.ecommerce.service;

import sn.aziz.ecommerce.dto.*;
import java.util.List;

public interface CommandeService {
    CommandeDTO creer(String email, CreerCommandeRequest request);
    CommandeDTO confirmerPaiement(String email, Long commandeId);
    List<CommandeDTO> mesCommandes(String email);
    CommandeDTO obtenir(String email, Long commandeId);
}
