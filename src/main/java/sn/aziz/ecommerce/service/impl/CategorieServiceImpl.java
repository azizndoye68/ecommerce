package sn.aziz.ecommerce.service.impl;

import sn.aziz.ecommerce.dto.CategorieDTO;
import sn.aziz.ecommerce.entity.Categorie;
import sn.aziz.ecommerce.exception.RessourceNonTrouveeException;
import sn.aziz.ecommerce.mapper.CategorieMapper;
import sn.aziz.ecommerce.repository.CategorieRepository;
import sn.aziz.ecommerce.service.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategorieServiceImpl implements CategorieService {

    private final CategorieRepository categorieRepository;
    private final CategorieMapper categorieMapper;

    @Override
    public CategorieDTO creer(CategorieDTO dto) {
        if (categorieRepository.existsByNom(dto.getNom())) {
            throw new IllegalArgumentException("Une catégorie avec ce nom existe déjà");
        }
        Categorie categorie = categorieMapper.toEntity(dto);
        return categorieMapper.toDTO(categorieRepository.save(categorie));
    }

    @Override
    public CategorieDTO obtenirParId(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Catégorie introuvable avec l'id : " + id));
        return categorieMapper.toDTO(categorie);
    }

    @Override
    public List<CategorieDTO> obtenirToutes() {
        return categorieRepository.findAll().stream()
                .map(categorieMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategorieDTO modifier(Long id, CategorieDTO dto) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Catégorie introuvable avec l'id : " + id));
        categorie.setNom(dto.getNom());
        categorie.setDescription(dto.getDescription());
        return categorieMapper.toDTO(categorieRepository.save(categorie));
    }

    @Override
    public void supprimer(Long id) {
        if (!categorieRepository.findById(id).isPresent()) {
            throw new RessourceNonTrouveeException("Catégorie introuvable avec l'id : " + id);
        }
        categorieRepository.deleteById(id);
    }
}