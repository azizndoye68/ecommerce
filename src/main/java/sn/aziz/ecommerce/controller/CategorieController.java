package sn.aziz.ecommerce.controller;

import jakarta.validation.Valid;
import sn.aziz.ecommerce.dto.CategorieDTO;
import sn.aziz.ecommerce.service.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    @PostMapping
    public ResponseEntity<CategorieDTO> creer(@Valid @RequestBody CategorieDTO dto) {
        CategorieDTO cree = categorieService.creer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieDTO> obtenirParId(@PathVariable Long id) {
        return ResponseEntity.ok(categorieService.obtenirParId(id));
    }

    @GetMapping
    public ResponseEntity<List<CategorieDTO>> obtenirToutes() {
        return ResponseEntity.ok(categorieService.obtenirToutes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategorieDTO> modifier(
            @PathVariable Long id, @Valid @RequestBody CategorieDTO dto) {
        return ResponseEntity.ok(categorieService.modifier(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        categorieService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}