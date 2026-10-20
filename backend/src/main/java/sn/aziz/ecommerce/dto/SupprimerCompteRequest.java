package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupprimerCompteRequest {
    @NotBlank
    private String motDePasse;
}
