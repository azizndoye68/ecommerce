package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurUpdateRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Pattern(regexp = "^[\\p{L}\\p{M}]+(?:[ '’\\-][\\p{L}\\p{M}]+)*$", message = "Le nom doit contenir uniquement des lettres")
    private String nom;
    @NotBlank(message = "Le prénom est obligatoire")
    @Pattern(regexp = "^[\\p{L}\\p{M}]+(?:[ '’\\-][\\p{L}\\p{M}]+)*$", message = "Le prénom doit contenir uniquement des lettres")
    private String prenom;
    @Pattern(regexp = "^(?:[0-9]{9})?$", message = "Le téléphone doit contenir exactement 9 chiffres")
    private String telephone;
    private String adresse;

}
