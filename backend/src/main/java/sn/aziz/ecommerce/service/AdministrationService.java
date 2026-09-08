package sn.aziz.ecommerce.service;

import sn.aziz.ecommerce.dto.CreerAdministrateurRequest;
import sn.aziz.ecommerce.dto.StatutUtilisateurRequest;
import sn.aziz.ecommerce.dto.UtilisateurDTO;
import java.util.List;

public interface AdministrationService {
    UtilisateurDTO creerAdministrateur(String acteurEmail, CreerAdministrateurRequest request);
    UtilisateurDTO modifierStatut(String acteurEmail, Long id, StatutUtilisateurRequest request);
    List<UtilisateurDTO> listerAdministrateurs();
}
