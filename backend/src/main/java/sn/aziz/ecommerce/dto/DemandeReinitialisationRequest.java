package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DemandeReinitialisationRequest {
    @NotBlank @Email
    private String email;
}
