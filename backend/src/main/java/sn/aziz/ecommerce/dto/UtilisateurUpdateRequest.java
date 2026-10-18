package sn.aziz.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurUpdateRequest {

    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;

}
