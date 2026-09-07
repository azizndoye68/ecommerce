package sn.aziz.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sn.aziz.ecommerce.dto.*;
import sn.aziz.ecommerce.service.CommandeService;
import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeController {
    private final CommandeService commandeService;
    @PostMapping public ResponseEntity<CommandeDTO> creer(@AuthenticationPrincipal UserDetails utilisateur, @Valid @RequestBody CreerCommandeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandeService.creer(utilisateur.getUsername(), request));
    }
    @PostMapping("/{id}/paiement/confirmer") public ResponseEntity<CommandeDTO> confirmer(@AuthenticationPrincipal UserDetails utilisateur, @PathVariable Long id) {
        return ResponseEntity.ok(commandeService.confirmerPaiement(utilisateur.getUsername(), id));
    }
    @GetMapping public ResponseEntity<List<CommandeDTO>> lister(@AuthenticationPrincipal UserDetails utilisateur) {
        return ResponseEntity.ok(commandeService.mesCommandes(utilisateur.getUsername()));
    }
    @GetMapping("/{id}") public ResponseEntity<CommandeDTO> obtenir(@AuthenticationPrincipal UserDetails utilisateur, @PathVariable Long id) {
        return ResponseEntity.ok(commandeService.obtenir(utilisateur.getUsername(), id));
    }
}
