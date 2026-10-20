package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReinitialiserMotDePasseRequest {
    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 8, max = 100, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String nouveauMotDePasse;
}
