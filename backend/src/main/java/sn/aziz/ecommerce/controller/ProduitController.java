package sn.aziz.ecommerce.controller;

import jakarta.validation.Valid;
import sn.aziz.ecommerce.dto.ProduitDTO;
import sn.aziz.ecommerce.service.ProduitService;
import sn.aziz.ecommerce.service.ImageProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    private final ImageProduitService imageProduitService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.Map<String, String>> televerserImage(@RequestParam("fichier") MultipartFile fichier) {
        return ResponseEntity.ok(java.util.Map.of("imageUrl", imageProduitService.televerser(fichier)));
    }

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
    public ResponseEntity<Page<ProduitDTO>> obtenirTous(
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {
        return ResponseEntity.ok(produitService.obtenirTous(pageable));
    }

    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<Page<ProduitDTO>> obtenirParCategorie(
            @PathVariable Long categorieId,
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {
        return ResponseEntity.ok(produitService.obtenirParCategorie(categorieId, pageable));
    }

    @GetMapping("/recherche")
    public ResponseEntity<Page<ProduitDTO>> rechercher(
            @RequestParam String nom,
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {
        return ResponseEntity.ok(produitService.rechercherParNom(nom, pageable));
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
