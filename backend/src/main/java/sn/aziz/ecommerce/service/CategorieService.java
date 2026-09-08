package sn.aziz.ecommerce.service;

import sn.aziz.ecommerce.dto.CategorieDTO;

import java.util.List;

public interface CategorieService {

    CategorieDTO creer(CategorieDTO dto);

    CategorieDTO obtenirParId(Long id);

    List<CategorieDTO> obtenirToutes();

    CategorieDTO modifier(Long id, CategorieDTO dto);

    void supprimer(Long id);
}