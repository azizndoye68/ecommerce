package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sn.aziz.ecommerce.entity.ModePaiement;

@Data
public class CreerCommandeRequest {
    @NotNull private ModePaiement modePaiement;
}
