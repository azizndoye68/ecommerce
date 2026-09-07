package sn.aziz.ecommerce.mapper;

import sn.aziz.ecommerce.dto.CategorieDTO;
import sn.aziz.ecommerce.entity.Categorie;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategorieMapper {

    CategorieDTO toDTO(Categorie categorie);

    Categorie toEntity(CategorieDTO dto);
}