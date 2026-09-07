package sn.aziz.ecommerce.mapper;

import sn.aziz.ecommerce.dto.UtilisateurDTO;
import sn.aziz.ecommerce.entity.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    UtilisateurDTO toDTO(Utilisateur utilisateur);

    @org.mapstruct.Named("roleToString")
    default String roleToString(sn.aziz.ecommerce.entity.Role role) {
        return role != null ? role.name() : null;
    }
}