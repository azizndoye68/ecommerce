package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangerMotDePasseRequest {
    @NotBlank(message = "Le mot de passe actuel est obligatoire")
    private String motDePasseActuel;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @Size(min = 6, message = "Le nouveau mot de passe doit contenir au moins 6 caractères")
    private String nouveauMotDePasse;
}
