package sn.aziz.ecommerce.controller;

import jakarta.validation.Valid;
import sn.aziz.ecommerce.dto.ProduitDTO;
import sn.aziz.ecommerce.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    @PostMapping
    public ResponseEntity<ProduitDTO> creer(@Valid @RequestBody ProduitDTO produitDTO) {
        ProduitDTO cree = produitService.creer(produitDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitDTO> obtenirParId(@PathVariable Long id) {
        return ResponseEntity.ok(produitService.obtenirParId(id));
    }

    @GetMapping
    public ResponseEntity<List<ProduitDTO>> obtenirTous() {
        return ResponseEntity.ok(produitService.obtenirTous());
    }

    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<ProduitDTO>> obtenirParCategorie(@PathVariable Long categorieId) {
        return ResponseEntity.ok(produitService.obtenirParCategorie(categorieId));
    }

    @GetMapping("/recherche")
    public ResponseEntity<List<ProduitDTO>> rechercher(@RequestParam String nom) {
        return ResponseEntity.ok(produitService.rechercherParNom(nom));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProduitDTO> modifier(
            @PathVariable Long id, @Valid @RequestBody ProduitDTO produitDTO) {
        return ResponseEntity.ok(produitService.modifier(id, produitDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        produitService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}