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
import sn.aziz.ecommerce.dto.CommandeDTO;
import sn.aziz.ecommerce.dto.StatutCommandeRequest;
import sn.aziz.ecommerce.service.CommandeService;

@RestController
@RequestMapping("/api/admin/commandes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class GestionCommandeAdminController {
    private final CommandeService commandeService;

    @PatchMapping("/{id}/statut")
    public ResponseEntity<CommandeDTO> modifierStatut(@PathVariable Long id,
            @Valid @RequestBody StatutCommandeRequest request) {
        return ResponseEntity.ok(commandeService.modifierStatutLivraison(id, request.getStatut()));
    }
}
