package sn.aziz.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sn.aziz.ecommerce.dto.*;
import sn.aziz.ecommerce.service.PanierService;

@RestController
@RequestMapping("/api/panier")
@RequiredArgsConstructor
public class PanierController {
    private final PanierService panierService;
    @GetMapping public ResponseEntity<PanierDTO> obtenir(@AuthenticationPrincipal UserDetails utilisateur) {
        return ResponseEntity.ok(panierService.obtenirPanierConnecte(utilisateur.getUsername()));
    }
    @PostMapping("/lignes") public ResponseEntity<PanierDTO> ajouter(@AuthenticationPrincipal UserDetails utilisateur, @Valid @RequestBody AjouterAuPanierRequest request) {
        return ResponseEntity.ok(panierService.ajouter(utilisateur.getUsername(), request));
    }
    @PatchMapping("/lignes/{produitId}") public ResponseEntity<PanierDTO> modifier(@AuthenticationPrincipal UserDetails utilisateur, @PathVariable Long produitId, @Valid @RequestBody ModifierQuantitePanierRequest request) {
        return ResponseEntity.ok(panierService.modifierQuantite(utilisateur.getUsername(), produitId, request));
    }
    @DeleteMapping("/lignes/{produitId}") public ResponseEntity<Void> supprimer(@AuthenticationPrincipal UserDetails utilisateur, @PathVariable Long produitId) {
        panierService.supprimerLigne(utilisateur.getUsername(), produitId); return ResponseEntity.noContent().build();
    }
    @DeleteMapping public ResponseEntity<Void> vider(@AuthenticationPrincipal UserDetails utilisateur) {
        panierService.vider(utilisateur.getUsername()); return ResponseEntity.noContent().build();
    }
}
