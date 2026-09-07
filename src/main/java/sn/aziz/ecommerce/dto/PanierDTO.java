package sn.aziz.ecommerce.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder
public class PanierDTO {
    private Long id;
    private List<LignePanierDTO> lignes;
    private BigDecimal total;
}
