package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sn.aziz.ecommerce.entity.StatutCommande;

@Data
public class StatutCommandeRequest {
    @NotNull
    private StatutCommande statut;
}
