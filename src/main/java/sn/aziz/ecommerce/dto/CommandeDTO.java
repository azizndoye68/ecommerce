package sn.aziz.ecommerce.dto;

import lombok.Builder;
import lombok.Data;
import sn.aziz.ecommerce.entity.ModePaiement;
import sn.aziz.ecommerce.entity.StatutCommande;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class CommandeDTO {
    private Long id;
    private StatutCommande statut;
    private ModePaiement modePaiement;
    private BigDecimal total;
    private List<LigneCommandeDTO> lignes;
    private String referencePaiement;
    private String urlPaiement;
    private LocalDateTime creeLe;
}
