package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AjouterAuPanierRequest {
    @NotNull private Long produitId;
    @NotNull @Min(1) private Integer quantite;
}
