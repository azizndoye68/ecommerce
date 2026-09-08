package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.Size;
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

    @Size(min = 6, message = "Le mot de passe doit contenir au moins 12 caractères")
    private String motDePasse;
}
