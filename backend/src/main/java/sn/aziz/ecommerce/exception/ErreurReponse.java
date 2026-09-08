package sn.aziz.ecommerce.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErreurReponse {

    private LocalDateTime timestamp;
    private int statut;
    private String message;
    private String chemin;
}