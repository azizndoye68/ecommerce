package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreerAdministrateurRequest {
    @NotBlank @Pattern(regexp = "^[\\p{L}\\p{M}]+(?:[ '’\\-][\\p{L}\\p{M}]+)*$") private String nom;
    @NotBlank @Pattern(regexp = "^[\\p{L}\\p{M}]+(?:[ '’\\-][\\p{L}\\p{M}]+)*$") private String prenom;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères") private String motDePasse;
    @Pattern(regexp = "^(?:[0-9]{9})?$") private String telephone;
    private String adresse;
}
