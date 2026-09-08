package sn.aziz.ecommerce.mapper;

import sn.aziz.ecommerce.dto.ProduitDTO;
import sn.aziz.ecommerce.entity.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProduitMapper {

    @Mapping(source = "categorie.id", target = "categorieId")
    @Mapping(source = "categorie.nom", target = "categorieNom")
    ProduitDTO toDTO(Produit produit);

    @Mapping(target = "categorie", ignore = true)
    Produit toEntity(ProduitDTO dto);
}