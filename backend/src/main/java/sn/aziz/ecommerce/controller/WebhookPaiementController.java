package sn.aziz.ecommerce.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sn.aziz.ecommerce.entity.ModePaiement;
import sn.aziz.ecommerce.service.CommandeService;

/** Webhook UniTech Pay. L'état payé n'est appliqué qu'après vérification HMAC. */
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookPaiementController {
    private static final String SIGNATURE_HEADER = "X-UNITECHPAY-SIGNATURE";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final CommandeService commandeService;

    @Value("${unitechpay.api-key:}") private String apiKey;

    @PostMapping("/unitechpay")
    public ResponseEntity<Void> unitechPay(
            @RequestHeader(value = SIGNATURE_HEADER, required = false) String headerSignature,
            @RequestBody String rawBody) {
        JsonNode payload;
        try {
            payload = OBJECT_MAPPER.readTree(rawBody);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload UniTech Pay invalide");
        }

        String event = texte(payload, "event");
        String status = texte(payload, "status");
        String method = premiereValeur(payload, "method", "payment_method", "source", "channel", "operator");
        log.info("Notification UniTech reçue (event={}, status={}, method={})", event, status, method);

        try {
            verifierSignature(rawBody, payload, headerSignature);
        } catch (ResponseStatusException ex) {
            log.warn("Webhook UniTech Pay refusé: {}", ex.getReason());
            throw ex;
        }
        if ("payment_failed".equalsIgnoreCase(event) || "payment_expired".equalsIgnoreCase(event)) {
            log.info("Webhook UniTech Pay reçu: événement {}", event);
            return ResponseEntity.noContent().build();
        }
        boolean evenementPaiement = "payment_completed".equalsIgnoreCase(event)
                || "payment_updated".equalsIgnoreCase(event);
        if (!evenementPaiement || !"completed".equalsIgnoreCase(status)) {
            log.warn("Webhook UniTech Pay ignoré: événement/statut non final (event={}, status={})", event, status);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Événement de paiement UniTech Pay non reconnu");
        }

        String reference = texte(payload, "reference");
        JsonNode amountNode = payload.get("amount");
        if (reference == null || amountNode == null || (!amountNode.isNumber() && !amountNode.isTextual())) {
            log.warn("Webhook UniTech Pay incomplet: referencePrésente={}, amountPrésent={}, champs={}",
                    reference != null, amountNode != null, nomsChamps(payload));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Référence ou montant manquant/invalide");
        }

        BigDecimal montant;
        try {
            montant = amountNode.isNumber() ? amountNode.decimalValue() : new BigDecimal(amountNode.asText());
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Montant du webhook invalide");
        }

        ModePaiement mode = convertirMode(method);
        if (method != null && mode == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Moyen de paiement UniTech Pay inconnu");
        if (mode == null) log.info("Méthode absente du webhook; vérification avec le moyen enregistré pour la commande");
        try {
            commandeService.confirmerPaiementUnitech(reference, montant, mode);
        } catch (RuntimeException ex) {
            log.warn("Webhook UniTech Pay valide mais commande non confirmée: {}", ex.getMessage());
            throw ex;
        }
        log.info("Paiement UniTech Pay confirmé (canal {})", mode);
        return ResponseEntity.noContent().build();
    }

    private void verifierSignature(String rawBody, JsonNode payload, String headerSignature) {
        if (apiKey == null || apiKey.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Webhook UniTech Pay non configuré");

        String signature = headerSignature;
        String contenuSigne = rawBody;
        if (signature == null || signature.isBlank()) {
            signature = texte(payload, "signature");
            String event = texte(payload, "event");
            String reference = texte(payload, "reference");
            String amount = texte(payload, "amount");
            String status = texte(payload, "status");
            String signedAt = texte(payload, "signed_at");
            if (event == null || reference == null || amount == null || status == null || signedAt == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Signature UniTech Pay manquante");
            contenuSigne = String.join("|", event, reference, amount, status, signedAt);
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(apiKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String attendue = HexFormat.of().formatHex(mac.doFinal(contenuSigne.getBytes(StandardCharsets.UTF_8)));
            String fournie = signature.trim().replaceFirst("(?i)^sha256=", "").toLowerCase();
            if (!MessageDigest.isEqual(attendue.getBytes(StandardCharsets.US_ASCII), fournie.getBytes(StandardCharsets.US_ASCII)))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Signature UniTech Pay invalide");
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Vérification du webhook impossible");
        }
    }

    private String texte(JsonNode node, String field) {
        JsonNode value = node == null ? null : node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private String premiereValeur(JsonNode node, String... fields) {
        for (String field : fields) {
            String value = texte(node, field);
            if (value != null && !value.isBlank()) return value;
        }
        return null;
    }

    /** Journalise uniquement la structure du webhook, jamais ses valeurs ni sa signature. */
    private List<String> nomsChamps(JsonNode node) {
        List<String> noms = new ArrayList<>();
        if (node != null && node.isObject()) {
            Iterator<String> champs = node.fieldNames();
            champs.forEachRemaining(noms::add);
        }
        return noms;
    }

    private ModePaiement convertirMode(String method) {
        if (method == null || method.isBlank()) return null;
        return switch (method.trim().toLowerCase().replace(' ', '_')) {
            case "wave", "wave_money" -> ModePaiement.WAVE;
            case "orange", "orange_money", "orange_om", "orange_maxit" -> ModePaiement.ORANGE_MONEY;
            default -> null;
        };
    }
}
