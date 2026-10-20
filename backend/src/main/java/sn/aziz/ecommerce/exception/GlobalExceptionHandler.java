package sn.aziz.ecommerce.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RessourceNonTrouveeException.class)
    public ResponseEntity<ErreurReponse> gererRessourceNonTrouvee(
            RessourceNonTrouveeException ex, HttpServletRequest request) {

        ErreurReponse erreur = new ErreurReponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erreur);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> gererErreursValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> erreurs = new HashMap<>();
        for (FieldError erreur : ex.getBindingResult().getFieldErrors()) {
            erreurs.put(erreur.getField(), erreur.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreurs);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurReponse> gererExceptionGenerale(
            Exception ex, HttpServletRequest request) {

        ErreurReponse erreur = new ErreurReponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Une erreur interne est survenue",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErreurReponse> gererStatutHttp(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus statut = HttpStatus.valueOf(ex.getStatusCode().value());
        return erreur(statut, ex.getReason() == null ? "La requête ne peut pas être traitée." : ex.getReason(), request);
    }

    @ExceptionHandler(PaiementIndisponibleException.class)
    public ResponseEntity<ErreurReponse> gererPaiementIndisponible(
            PaiementIndisponibleException ex, HttpServletRequest request) {
        return erreur(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErreurReponse> gererAccesRefuse(AccessDeniedException ex, HttpServletRequest request) {
        return erreur(HttpStatus.FORBIDDEN, "Accès refusé", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErreurReponse> gererAuthentification(AuthenticationException ex, HttpServletRequest request) {
        return erreur(HttpStatus.UNAUTHORIZED, "Authentification requise ou invalide", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErreurReponse> gererIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        ErreurReponse erreur = new ErreurReponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErreurReponse> gererRessourceStatique(
            NoResourceFoundException ex, HttpServletRequest request) {

        ErreurReponse erreur = new ErreurReponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Ressource non trouvée",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erreur);
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ErreurReponse> gererBadCredentials(
            org.springframework.security.authentication.BadCredentialsException ex,
            HttpServletRequest request) {

        ErreurReponse erreur = new ErreurReponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Email ou mot de passe incorrect",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erreur);
    }

    private ResponseEntity<ErreurReponse> erreur(HttpStatus statut, String message, HttpServletRequest request) {
        return ResponseEntity.status(statut).body(new ErreurReponse(
                LocalDateTime.now(), statut.value(), message, request.getRequestURI()));
    }
}
