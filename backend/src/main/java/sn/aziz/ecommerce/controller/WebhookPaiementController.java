package sn.aziz.ecommerce.controller;

import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.aziz.ecommerce.dto.CommandeDTO;
import sn.aziz.ecommerce.dto.WebhookPaiementRequest;
import sn.aziz.ecommerce.entity.ModePaiement;
import sn.aziz.ecommerce.service.CommandeService;

/**
 * Points d'intégration volontairement neutres : l'adaptateur Wave/Orange doit valider la signature
 * native du fournisseur puis transmettre la référence de paiement avec le secret configuré.
 */
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookPaiementController {
    private final CommandeService commandeService;

    @Value("${wave.webhook-secret:}") private String waveSecret;
    @Value("${orange-money.webhook-secret:}") private String orangeMoneySecret;

    @PostMapping("/wave")
    public ResponseEntity<CommandeDTO> wave(@RequestHeader(value = "X-Webhook-Secret", required = false) String secret,
                                             @Valid @RequestBody WebhookPaiementRequest request) {
        verifierSecret(secret, waveSecret);
        return ResponseEntity.ok(commandeService.confirmerPaiementMarchand(ModePaiement.WAVE, request.getReferencePaiement()));
    }

    @PostMapping("/orange-money")
    public ResponseEntity<CommandeDTO> orangeMoney(@RequestHeader(value = "X-Webhook-Secret", required = false) String secret,
                                                    @Valid @RequestBody WebhookPaiementRequest request) {
        verifierSecret(secret, orangeMoneySecret);
        return ResponseEntity.ok(commandeService.confirmerPaiementMarchand(ModePaiement.ORANGE_MONEY, request.getReferencePaiement()));
    }

    private void verifierSecret(String fourni, String attendu) {
        if (attendu == null || attendu.isBlank() || fourni == null ||
                !MessageDigest.isEqual(attendu.getBytes(StandardCharsets.UTF_8), fourni.getBytes(StandardCharsets.UTF_8))) {
            throw new AccessDeniedException("Secret de webhook invalide");
        }
    }
}
