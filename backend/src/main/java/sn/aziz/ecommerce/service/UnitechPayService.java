package sn.aziz.ecommerce.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;
import sn.aziz.ecommerce.entity.ModePaiement;
import sn.aziz.ecommerce.exception.PaiementIndisponibleException;

/** Adaptateur serveur vers UniTech Pay pour Wave et Orange Money au Sénégal. */
@Service
@Slf4j
public class UnitechPayService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @org.springframework.beans.factory.annotation.Value("${unitechpay.base-url:https://api.unitech.sn/api.php}") private String baseUrl;
    @org.springframework.beans.factory.annotation.Value("${unitechpay.api-key:}") private String apiKey;
    @org.springframework.beans.factory.annotation.Value("${unitechpay.orange-action:create_orange_om}") private String orangeAction;
    @org.springframework.beans.factory.annotation.Value("${unitechpay.callback-success:}") private String callbackSuccess;
    @org.springframework.beans.factory.annotation.Value("${unitechpay.callback-cancel:}") private String callbackCancel;

    public PaiementInitialisation initialiser(ModePaiement mode, Long commandeId, BigDecimal total, String telephone) {
        verifierConfiguration();
        if (mode != ModePaiement.WAVE && mode != ModePaiement.ORANGE_MONEY)
            throw new IllegalArgumentException("UniTech Pay ne prend en charge que Wave et Orange Money ici");

        String action = mode == ModePaiement.WAVE ? "create_wave_payment" : orangeAction;
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("amount", total.setScale(0, RoundingMode.HALF_UP));
        body.put("description", "Commande #" + commandeId);
        ajouterCallbackHttps(body, "callback_success", callbackSuccess);
        ajouterCallbackHttps(body, "callback_cancel", callbackCancel);
        if (telephone == null || telephone.isBlank())
            throw new PaiementIndisponibleException("Le numéro de téléphone de livraison est requis pour le paiement");
        body.put("customer_number", telephone);

        String uri = null;
        String jsonResponse = null;
        try {
            uri = UriComponentsBuilder.fromUriString(baseUrl).queryParam("action", action).build().toUriString();
            String jsonRequest = OBJECT_MAPPER.writeValueAsString(body);
            jsonResponse = RestClient.create().post().uri(uri)
                    .headers(headers -> headers.setBearerAuth(apiKey))
                    .contentType(MediaType.APPLICATION_JSON).body(jsonRequest).retrieve().body(String.class);
            JsonNode response = jsonResponse == null ? null : OBJECT_MAPPER.readTree(jsonResponse);
            if (response == null)
                throw new PaiementIndisponibleException("UniTech Pay a renvoyé une réponse vide");
            if (!response.path("success").asBoolean(false)) {
                String message = response.path("message").asText("");
                throw new PaiementIndisponibleException(message.isBlank()
                        ? "UniTech Pay a refusé la création du paiement"
                        : "UniTech Pay : " + message.substring(0, Math.min(message.length(), 240)));
            }
            JsonNode data = response == null ? null : response.path("data");
            String reference = valeur(data, "reference");
            String url = valeur(data, "payment_url");
            if (reference == null || url == null || !url.startsWith("https://"))
                throw new PaiementIndisponibleException("Réponse UniTech Pay invalide");
            return new PaiementInitialisation(reference, url);
        } catch (PaiementIndisponibleException ex) {
            throw ex;
        } catch (RestClientResponseException ex) {
            throw new PaiementIndisponibleException(messageErreurFournisseur(ex));
        } catch (ResourceAccessException ex) {
            throw new PaiementIndisponibleException("Impossible de joindre UniTech Pay. Vérifie ta connexion Internet et l'accès à api.unitech.sn.");
        } catch (JsonProcessingException ex) {
            String extrait = jsonResponse == null ? "<réponse vide>" : jsonResponse
                    .replaceAll("[\\r\\n\\t]+", " ").substring(0, Math.min(jsonResponse.length(), 500));
            log.warn("Réponse UniTech Pay non JSON pour {}: {}. Début de la réponse: {}",
                    uri, ex.getOriginalMessage(), extrait);
            throw new PaiementIndisponibleException(
                    "UniTech Pay a renvoyé une réponse illisible. Vérifie dans Render que UNITECHPAY_BASE_URL vaut exactement https://api.unitech.sn/api.php.");
        } catch (Exception ex) {
            throw new PaiementIndisponibleException("Erreur de communication avec UniTech Pay (" + ex.getClass().getSimpleName() + ")");
        }
    }

    private void ajouterCallbackHttps(Map<String, Object> body, String champ, String url) {
        if (url == null || url.isBlank()) return;
        try {
            URI uri = URI.create(url);
            if ("https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null)
                body.put(champ, url);
        } catch (IllegalArgumentException ignored) {
            // Les callbacks sont facultatifs; une URL locale ou invalide est omise.
        }
    }

    private String messageErreurFournisseur(RestClientResponseException ex) {
        String prefix = "UniTech Pay a refusé la demande (HTTP " + ex.getStatusCode().value() + ")";
        try {
            JsonNode body = OBJECT_MAPPER.readTree(ex.getResponseBodyAsString());
            String message = body.path("message").asText("");
            return message.isBlank() ? prefix : prefix + " : " + message.substring(0, Math.min(message.length(), 240));
        } catch (Exception ignored) {
            return prefix;
        }
    }

    private String valeur(JsonNode node, String champ) {
        if (node == null) return null;
        String value = node.path(champ).asText(null);
        return value == null || value.isBlank() ? null : value;
    }

    private void verifierConfiguration() {
        if (apiKey == null || apiKey.isBlank() || baseUrl == null || baseUrl.isBlank())
            throw new PaiementIndisponibleException("UniTech Pay n'est pas configuré : clé API manquante");
        if (!"create_orange_om".equals(orangeAction) && !"create_orange_maxit".equals(orangeAction))
            throw new PaiementIndisponibleException("Action Orange Money UniTech Pay invalide");
    }
}
