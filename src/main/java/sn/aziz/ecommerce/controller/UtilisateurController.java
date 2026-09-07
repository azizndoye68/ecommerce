package sn.aziz.ecommerce.controller;

import jakarta.validation.Valid;
import sn.aziz.ecommerce.dto.UtilisateurDTO;
import sn.aziz.ecommerce.dto.UtilisateurUpdateRequest;
import sn.aziz.ecommerce.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UtilisateurDTO>> obtenirTous() {
        return ResponseEntity.ok(utilisateurService.obtenirTous());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @utilisateurSecurity.estProprietaire(#id, authentication)")
    public ResponseEntity<UtilisateurDTO> obtenirParId(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.obtenirParId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @utilisateurSecurity.estProprietaire(#id, authentication)")
    public ResponseEntity<UtilisateurDTO> modifier(
            @PathVariable Long id, @Valid @RequestBody UtilisateurUpdateRequest request) {
        return ResponseEntity.ok(utilisateurService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        utilisateurService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}