package sn.aziz.ecommerce.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class LignePanierDTO {
    private Long produitId;
    private String nom;
    private String imageUrl;
    private BigDecimal prixUnitaire;
    private Integer quantite;
    private BigDecimal sousTotal;
}
