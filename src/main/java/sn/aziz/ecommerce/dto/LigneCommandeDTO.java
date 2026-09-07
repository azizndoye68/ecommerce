package sn.aziz.ecommerce.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class LigneCommandeDTO {
    private Long produitId;
    private String nomProduit;
    private BigDecimal prixUnitaire;
    private Integer quantite;
    private BigDecimal sousTotal;
}
