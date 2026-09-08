package sn.aziz.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sn.aziz.ecommerce.dto.*;
import sn.aziz.ecommerce.service.AdministrationService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/administrateurs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdministrationController {
    private final AdministrationService service;
    @PostMapping public ResponseEntity<UtilisateurDTO> creer(@AuthenticationPrincipal UserDetails acteur, @Valid @RequestBody CreerAdministrateurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.creerAdministrateur(acteur.getUsername(), request));
    }
    @GetMapping public ResponseEntity<List<UtilisateurDTO>> lister() { return ResponseEntity.ok(service.listerAdministrateurs()); }
    @PatchMapping("/{id}/statut") public ResponseEntity<UtilisateurDTO> statut(@AuthenticationPrincipal UserDetails acteur, @PathVariable Long id, @Valid @RequestBody StatutUtilisateurRequest request) {
        return ResponseEntity.ok(service.modifierStatut(acteur.getUsername(), id, request));
    }
}
