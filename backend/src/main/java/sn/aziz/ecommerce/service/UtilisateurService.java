package sn.aziz.ecommerce.service;

import sn.aziz.ecommerce.dto.UtilisateurDTO;
import sn.aziz.ecommerce.dto.UtilisateurUpdateRequest;

import java.util.List;

public interface UtilisateurService {

    List<UtilisateurDTO> obtenirTous();

    UtilisateurDTO obtenirParId(Long id);

    UtilisateurDTO obtenirMonProfil(String email);

    UtilisateurDTO modifierMonProfil(String email, UtilisateurUpdateRequest request);

    void changerMonMotDePasse(String email, String motDePasseActuel, String nouveauMotDePasse);

    UtilisateurDTO modifier(Long id, UtilisateurUpdateRequest request);

    void supprimer(Long id);
}
