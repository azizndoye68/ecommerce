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

@Service
public class WaveService {
    @Value("${wave.base-url}") private String baseUrl;
    @Value("${wave.api-key}") private String apiKey;
    @Value("${wave.currency}") private String currency;
    @Value("${wave.success-url}") private String successUrl;
    @Value("${wave.error-url}") private String errorUrl;

    public PaiementInitialisation creerSession(Long commandeId, BigDecimal total) {
        verifierConfiguration();
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("amount", montant(total)); body.put("currency", currency);
            body.put("client_reference", "commande-" + commandeId);
            body.put("success_url", successUrl); body.put("error_url", errorUrl);
            JsonNode response = RestClient.create(baseUrl).post().uri("/v1/checkout/sessions")
                    .headers(h -> h.setBearerAuth(apiKey)).contentType(MediaType.APPLICATION_JSON)
                    .body(body).retrieve().body(JsonNode.class);
            String id = response == null ? null : response.path("id").asText(null);
            String url = response == null ? null : response.path("wave_launch_url").asText(null);
            if (id == null || url == null || url.isBlank()) throw new PaiementIndisponibleException("Réponse Wave invalide");
            return new PaiementInitialisation(id, url);
        } catch (PaiementIndisponibleException ex) { throw ex; }
        catch (Exception ex) { throw new PaiementIndisponibleException("Le service Wave est indisponible"); }
    }

    public boolean estPaye(String reference) {
        verifierConfiguration();
        try {
            JsonNode response = RestClient.create(baseUrl).get().uri("/v1/checkout/sessions/{id}", reference)
                    .headers(h -> h.setBearerAuth(apiKey)).retrieve().body(JsonNode.class);
            return response != null && "complete".equals(response.path("checkout_status").asText())
                    && "succeeded".equals(response.path("payment_status").asText());
        } catch (Exception ex) { throw new PaiementIndisponibleException("Vérification Wave impossible"); }
    }
    private void verifierConfiguration() {
        if (apiKey == null || apiKey.isBlank() || successUrl == null || successUrl.isBlank() || errorUrl == null || errorUrl.isBlank())
            throw new PaiementIndisponibleException("Wave n'est pas configuré");
    }
    private String montant(BigDecimal total) {
        return "XOF".equals(currency) ? total.setScale(0, RoundingMode.HALF_UP).toPlainString() : total.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
