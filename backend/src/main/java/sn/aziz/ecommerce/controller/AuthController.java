package sn.aziz.ecommerce.controller;

import jakarta.validation.Valid;
import sn.aziz.ecommerce.dto.AuthResponse;
import sn.aziz.ecommerce.dto.ConnexionRequest;
import sn.aziz.ecommerce.dto.InscriptionRequest;
import sn.aziz.ecommerce.dto.RefreshTokenRequest;
import sn.aziz.ecommerce.dto.DemandeReinitialisationRequest;
import sn.aziz.ecommerce.dto.ReinitialiserMotDePasseRequest;
import sn.aziz.ecommerce.service.AuthService;
import sn.aziz.ecommerce.service.ReinitialisationMotDePasseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ReinitialisationMotDePasseService reinitialisationService;

    @PostMapping("/mot-de-passe-oublie")
    public ResponseEntity<java.util.Map<String, String>> demanderReinitialisation(
            @Valid @RequestBody DemandeReinitialisationRequest request) {
        return ResponseEntity.ok(java.util.Map.of("message", reinitialisationService.demander(request.getEmail())));
    }

    @PostMapping("/reinitialiser-mot-de-passe")
    public ResponseEntity<Void> reinitialiserMotDePasse(@Valid @RequestBody ReinitialiserMotDePasseRequest request) {
        reinitialisationService.reinitialiser(request.getToken(), request.getNouveauMotDePasse());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inscription")
    public ResponseEntity<AuthResponse> inscrire(@Valid @RequestBody InscriptionRequest request) {
        AuthResponse response = authService.inscrire(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/connexion")
    public ResponseEntity<AuthResponse> connecter(@Valid @RequestBody ConnexionRequest request) {
        AuthResponse response = authService.connecter(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rafraichir")
    public ResponseEntity<AuthResponse> rafraichir(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.rafraichir(request));
    }

    @PostMapping("/deconnexion")
    public ResponseEntity<Void> deconnecter(@RequestHeader("Authorization") String authorization,
            @RequestBody(required = false) RefreshTokenRequest request) {
        if (!authorization.startsWith("Bearer ")) throw new IllegalArgumentException("En-tête Authorization invalide");
        authService.deconnecter(authorization.substring(7), request == null ? null : request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
