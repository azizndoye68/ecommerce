package sn.aziz.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import sn.aziz.ecommerce.dto.CommandeDTO;
import sn.aziz.ecommerce.dto.StatutCommandeRequest;
import sn.aziz.ecommerce.service.CommandeService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/commandes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class GestionCommandeAdminController {
    private final CommandeService commandeService;

    @GetMapping
    public ResponseEntity<List<CommandeDTO>> lister() {
        return ResponseEntity.ok(commandeService.toutesLesCommandes());
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<CommandeDTO> modifierStatut(@PathVariable Long id,
            @Valid @RequestBody StatutCommandeRequest request,
            @AuthenticationPrincipal UserDetails administrateur) {
        CommandeDTO commande = commandeService.modifierStatutLivraison(id, request.getStatut());
        log.info("Statut de commande #{} modifié par {} vers {}", id, administrateur.getUsername(), commande.getStatut());
        return ResponseEntity.ok(commande);
    }
}
