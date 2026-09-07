package sn.aziz.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorieDTO {

    private Long id;

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    private String nom;

    private String description;
}