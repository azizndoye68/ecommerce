package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreerAdministrateurRequest {
    @NotBlank private String nom;
    @NotBlank private String prenom;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 12, message = "Le mot de passe doit contenir au moins 12 caractères") private String motDePasse;
    private String telephone;
    private String adresse;
}
