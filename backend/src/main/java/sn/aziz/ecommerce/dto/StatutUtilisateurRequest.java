package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatutUtilisateurRequest {
    @NotNull private Boolean actif;
}
