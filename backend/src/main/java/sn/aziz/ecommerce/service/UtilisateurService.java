package sn.aziz.ecommerce.service;

import sn.aziz.ecommerce.dto.UtilisateurDTO;
import sn.aziz.ecommerce.dto.UtilisateurUpdateRequest;

import java.util.List;

public interface UtilisateurService {

    List<UtilisateurDTO> obtenirTous();

    UtilisateurDTO obtenirParId(Long id);

    UtilisateurDTO modifier(Long id, UtilisateurUpdateRequest request);

    void supprimer(Long id);
}