package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdresseLivraisonRequest {
    @NotBlank private String destinataire;
    @NotBlank private String telephone;
    @NotBlank private String adresse;
    @NotBlank private String ville;
    @NotBlank private String pays;
}
