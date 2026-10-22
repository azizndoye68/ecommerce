package sn.aziz.ecommerce.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Set;

@Service
@Slf4j
public class ImageProduitService {
    private static final long TAILLE_MAX = 5L * 1024 * 1024;
    private static final Set<String> TYPES_AUTORISES = Set.of("image/png", "image/jpeg", "image/webp", "image/gif");

    private final RestClient restClient;

    @Value("${cloudinary.cloud-name:}") private String cloudName;
    @Value("${cloudinary.api-key:}") private String apiKey;
    @Value("${cloudinary.api-secret:}") private String apiSecret;

    public ImageProduitService() {
        this.restClient = RestClient.create();
    }

    public String televerser(MultipartFile fichier) {
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Le stockage des images n'est pas configuré. Ajoutez les variables Cloudinary au backend.");
        }
        if (fichier == null || fichier.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choisissez une image à envoyer.");
        }
        if (fichier.getSize() > TAILLE_MAX) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "L'image ne doit pas dépasser 5 Mo.");
        }
        String type = fichier.getContentType() == null ? "" : fichier.getContentType().toLowerCase(Locale.ROOT);
        if (!TYPES_AUTORISES.contains(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formats acceptés : PNG, JPEG, WebP et GIF.");
        }

        try {
            byte[] contenu = fichier.getBytes();
            verifierSignature(type, contenu);
            long timestamp = Instant.now().getEpochSecond();
            String signature = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1")
                    .digest(("folder=ecommerce/produits&timestamp=" + timestamp + apiSecret)
                            .getBytes(StandardCharsets.UTF_8)));

            MultiValueMap<String, Object> formulaire = new LinkedMultiValueMap<>();
            formulaire.add("file", new ByteArrayResource(contenu) {
                @Override public String getFilename() {
                    String nom = fichier.getOriginalFilename();
                    return nom == null || nom.isBlank() ? "produit" : nom.replaceAll("[^A-Za-z0-9._-]", "_");
                }
            });
            formulaire.add("api_key", apiKey);
            formulaire.add("timestamp", Long.toString(timestamp));
            formulaire.add("folder", "ecommerce/produits");
            formulaire.add("signature", signature);

            JsonNode resultat = restClient.post()
                    .uri("https://api.cloudinary.com/v1_1/{cloud}/image/upload", cloudName)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(formulaire)
                    .retrieve()
                    .body(JsonNode.class);
            JsonNode url = resultat == null ? null : resultat.get("secure_url");
            if (url == null || !url.isTextual()) throw new IllegalStateException("URL absente de la réponse Cloudinary");
            return url.asText();
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (RestClientResponseException exception) {
            String details = exception.getResponseBodyAsString();
            if (details.length() > 300) details = details.substring(0, 300);
            log.warn("Cloudinary a refusé l'envoi (HTTP {}). Réponse: {}", exception.getStatusCode().value(), details);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Cloudinary a refusé l’image (HTTP " + exception.getStatusCode().value() + "). Vérifiez le nom Cloud, la clé API et le secret API dans Render.");
        } catch (Exception exception) {
            log.error("Erreur technique lors de l'envoi d'une image vers Cloudinary", exception);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "L'envoi vers Cloudinary a échoué. Vérifiez ses identifiants et réessayez.");
        }
    }

    private void verifierSignature(String type, byte[] contenu) {
        boolean valide = switch (type) {
            case "image/png" -> contenu.length >= 8 && (contenu[0] & 0xff) == 0x89 && contenu[1] == 0x50 && contenu[2] == 0x4e && contenu[3] == 0x47;
            case "image/jpeg" -> contenu.length >= 3 && (contenu[0] & 0xff) == 0xff && (contenu[1] & 0xff) == 0xd8 && (contenu[2] & 0xff) == 0xff;
            case "image/gif" -> contenu.length >= 6 && new String(contenu, 0, 3, StandardCharsets.US_ASCII).equals("GIF");
            case "image/webp" -> contenu.length >= 12 && new String(contenu, 0, 4, StandardCharsets.US_ASCII).equals("RIFF") && new String(contenu, 8, 4, StandardCharsets.US_ASCII).equals("WEBP");
            default -> false;
        };
        if (!valide) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le fichier choisi n'est pas une image valide.");
    }
}
