package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.Data;
import sn.aziz.ecommerce.entity.ModePaiement;

@Data
public class CreerCommandeRequest {
    @NotNull private ModePaiement modePaiement;
    @NotNull @Valid private AdresseLivraisonRequest adresseLivraison;
}
