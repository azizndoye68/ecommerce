package sn.aziz.ecommerce.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import sn.aziz.ecommerce.exception.PaiementIndisponibleException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

/** Adaptateur pour l'URL Web Payment fournie par le contrat Orange Money du marchand. */
@Service
public class OrangeMoneyService {
    @Value("${orange-money.payment-url}") private String paymentUrl;
    @Value("${orange-money.access-token}") private String accessToken;
    @Value("${orange-money.currency}") private String currency;
    @Value("${orange-money.success-url}") private String successUrl;
    @Value("${orange-money.cancel-url}") private String cancelUrl;

    public PaiementInitialisation initialiser(Long commandeId, BigDecimal total) {
        verifierConfiguration();
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("order_id", "commande-" + commandeId); body.put("amount", total.setScale(0, RoundingMode.HALF_UP).toPlainString());
            body.put("currency", currency); body.put("return_url", successUrl); body.put("cancel_url", cancelUrl);
            JsonNode response = RestClient.create().post().uri(paymentUrl).headers(h -> h.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON).body(body).retrieve().body(JsonNode.class);
            String reference = valeur(response, "transaction_id", "id", "order_id");
            String url = valeur(response, "payment_url", "redirect_url", "url");
            if (reference == null || url == null) throw new PaiementIndisponibleException("Réponse Orange Money invalide : vérifiez le contrat API");
            return new PaiementInitialisation(reference, url);
        } catch (PaiementIndisponibleException ex) { throw ex; }
        catch (Exception ex) { throw new PaiementIndisponibleException("Le service Orange Money est indisponible"); }
    }
    private String valeur(JsonNode node, String... noms) {
        if (node == null) return null;
        for (String nom : noms) { String valeur = node.path(nom).asText(null); if (valeur != null && !valeur.isBlank()) return valeur; }
        return null;
    }
    private void verifierConfiguration() {
        if (paymentUrl == null || paymentUrl.isBlank() || accessToken == null || accessToken.isBlank() || successUrl == null || successUrl.isBlank() || cancelUrl == null || cancelUrl.isBlank())
            throw new PaiementIndisponibleException("Orange Money n'est pas configuré");
    }
}
