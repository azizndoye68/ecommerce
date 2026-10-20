package sn.aziz.ecommerce.controller;

import jakarta.validation.Valid;
import sn.aziz.ecommerce.dto.ChangerMotDePasseRequest;
import sn.aziz.ecommerce.dto.SupprimerCompteRequest;
import sn.aziz.ecommerce.dto.UtilisateurDTO;
import sn.aziz.ecommerce.dto.UtilisateurUpdateRequest;
import sn.aziz.ecommerce.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @GetMapping("/moi")
    public ResponseEntity<UtilisateurDTO> obtenirMonProfil(@AuthenticationPrincipal UserDetails utilisateur) {
        return ResponseEntity.ok(utilisateurService.obtenirMonProfil(utilisateur.getUsername()));
    }

    @PutMapping("/moi")
    public ResponseEntity<UtilisateurDTO> modifierMonProfil(
            @AuthenticationPrincipal UserDetails utilisateur,
            @Valid @RequestBody UtilisateurUpdateRequest request) {
        return ResponseEntity.ok(utilisateurService.modifierMonProfil(utilisateur.getUsername(), request));
    }

    @PutMapping("/moi/mot-de-passe")
    public ResponseEntity<Void> changerMonMotDePasse(
            @AuthenticationPrincipal UserDetails utilisateur,
            @Valid @RequestBody ChangerMotDePasseRequest request) {
        utilisateurService.changerMonMotDePasse(utilisateur.getUsername(), request.getMotDePasseActuel(), request.getNouveauMotDePasse());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/moi/suppression")
    public ResponseEntity<Void> supprimerMonCompte(
            @AuthenticationPrincipal UserDetails utilisateur,
            @Valid @RequestBody SupprimerCompteRequest request) {
        utilisateurService.supprimerMonCompte(utilisateur.getUsername(), request.getMotDePasse());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<UtilisateurDTO>> obtenirTous() {
        return ResponseEntity.ok(utilisateurService.obtenirTous());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or @utilisateurSecurity.estProprietaire(#id, authentication)")
    public ResponseEntity<UtilisateurDTO> obtenirParId(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.obtenirParId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or @utilisateurSecurity.estProprietaire(#id, authentication)")
    public ResponseEntity<UtilisateurDTO> modifier(
            @PathVariable Long id, @Valid @RequestBody UtilisateurUpdateRequest request) {
        return ResponseEntity.ok(utilisateurService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        utilisateurService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
