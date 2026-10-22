package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Pattern(regexp = "^[\\p{L}\\p{M}]+(?:[ '’\\-][\\p{L}\\p{M}]+)*$", message = "Le nom doit contenir uniquement des lettres")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Pattern(regexp = "^[\\p{L}\\p{M}]+(?:[ '’\\-][\\p{L}\\p{M}]+)*$", message = "Le prénom doit contenir uniquement des lettres")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    @Pattern(regexp = "^(?:[0-9]{9})?$", message = "Le téléphone doit contenir exactement 9 chiffres")
    private String telephone;

    private String adresse;
}
