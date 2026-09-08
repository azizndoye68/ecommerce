package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Format d'adaptation interne : le contrôleur du prestataire doit extraire ces champs de son payload signé. */
@Data
public class WebhookPaiementRequest {
    @NotBlank
    private String referencePaiement;
}
