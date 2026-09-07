package sn.aziz.ecommerce.controller;

import jakarta.validation.Valid;
import sn.aziz.ecommerce.dto.AuthResponse;
import sn.aziz.ecommerce.dto.ConnexionRequest;
import sn.aziz.ecommerce.dto.InscriptionRequest;
import sn.aziz.ecommerce.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}