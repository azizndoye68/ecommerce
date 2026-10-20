package sn.aziz.ecommerce.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
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

        verifierSignature(rawBody, payload, headerSignature);
        String event = texte(payload, "event");
        if ("payment_failed".equals(event) || "payment_expired".equals(event))
            return ResponseEntity.noContent().build();
        if (!"payment_completed".equals(event) || !"completed".equals(texte(payload, "status")))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Événement de paiement UniTech Pay non reconnu");

        String reference = texte(payload, "reference");
        String method = texte(payload, "method");
        JsonNode amountNode = payload.get("amount");
        if (reference == null || method == null || amountNode == null || !amountNode.isNumber())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Référence, méthode ou montant manquant");

        ModePaiement mode = switch (method.toLowerCase()) {
            case "wave" -> ModePaiement.WAVE;
            case "orange", "orange_money", "orange_om", "orange_maxit" -> ModePaiement.ORANGE_MONEY;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Moyen de paiement UniTech Pay inconnu");
        };
        commandeService.confirmerPaiementUnitech(reference, amountNode.decimalValue(), mode);
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
}
