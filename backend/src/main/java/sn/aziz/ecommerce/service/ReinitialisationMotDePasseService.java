package sn.aziz.ecommerce.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sn.aziz.ecommerce.entity.JetonReinitialisation;
import sn.aziz.ecommerce.entity.Utilisateur;
import sn.aziz.ecommerce.repository.JetonReinitialisationRepository;
import sn.aziz.ecommerce.repository.RefreshTokenRepository;
import sn.aziz.ecommerce.repository.UtilisateurRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ReinitialisationMotDePasseService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String MESSAGE_CONFIRMATION = "Si un compte correspond à cette adresse, un lien de réinitialisation lui a été envoyé.";

    private final UtilisateurRepository utilisateurRepository;
    private final JetonReinitialisationRepository jetonRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.host:}") private String serveurMail;
    @Value("${mail.from:${spring.mail.username:}}") private String expediteur;
    @Value("${password-reset.frontend-url:http://localhost:4200}") private String urlFrontend;

    @Transactional
    public String demander(String email) {
        verifierConfigurationMail();
        utilisateurRepository.findByEmailIgnoreCase(email.trim().toLowerCase(Locale.ROOT))
                .filter(Utilisateur::isEnabled)
                .ifPresent(this::creerEtEnvoyerLien);
        return MESSAGE_CONFIRMATION;
    }

    @Transactional
    public void reinitialiser(String jeton, String nouveauMotDePasse) {
        String hash = hacher(jeton);
        JetonReinitialisation demande = jetonRepository.findByJetonHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Ce lien est invalide ou a expiré. Demande un nouveau lien."));
        if (demande.getExpireLe().isBefore(LocalDateTime.now())) {
            jetonRepository.delete(demande);
            throw new IllegalArgumentException("Ce lien est invalide ou a expiré. Demande un nouveau lien.");
        }

        Utilisateur utilisateur = demande.getUtilisateur();
        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(utilisateur);
        jetonRepository.deleteByUtilisateurId(utilisateur.getId());
        refreshTokenRepository.deleteByUtilisateurId(utilisateur.getId());
    }

    @org.springframework.scheduling.annotation.Scheduled(fixedDelay = 86400000L)
    @Transactional
    public void purgerJetonsExpires() {
        jetonRepository.deleteByExpireLeBefore(LocalDateTime.now());
    }

    private void creerEtEnvoyerLien(Utilisateur utilisateur) {
        jetonRepository.deleteByUtilisateurId(utilisateur.getId());
        byte[] aleatoire = new byte[32];
        SECURE_RANDOM.nextBytes(aleatoire);
        String jeton = Base64.getUrlEncoder().withoutPadding().encodeToString(aleatoire);

        JetonReinitialisation demande = new JetonReinitialisation();
        demande.setJetonHash(hacher(jeton));
        demande.setUtilisateur(utilisateur);
        demande.setExpireLe(LocalDateTime.now().plusMinutes(30));
        jetonRepository.save(demande);

        String lien = urlFrontend.replaceAll("/$", "") + "/reinitialiser-mot-de-passe?token=" + jeton;
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setTo(utilisateur.getEmail());
            if (expediteur != null && !expediteur.isBlank()) helper.setFrom(expediteur);
            helper.setSubject("Réinitialisation de votre mot de passe SunuMarket");
            helper.setText("<p>Bonjour " + echapper(utilisateur.getPrenom()) + ",</p>"
                    + "<p>Une demande de réinitialisation du mot de passe de votre compte SunuMarket a été reçue.</p>"
                    + "<p><a href=\"" + lien + "\">Choisir un nouveau mot de passe</a></p>"
                    + "<p>Ce lien expire dans 30 minutes et ne peut être utilisé qu'une seule fois. "
                    + "Si vous n'êtes pas à l'origine de cette demande, ignorez cet e-mail.</p>", true);
            mailSender.send(message);
        } catch (MessagingException | org.springframework.mail.MailException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "L'envoi de l'e-mail a échoué. Vérifiez la configuration SMTP du backend.");
        }
    }

    private void verifierConfigurationMail() {
        if (serveurMail == null || serveurMail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "L'envoi d'e-mails n'est pas configuré sur le backend.");
        }
    }

    private String hacher(String valeur) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(valeur.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Impossible de sécuriser le jeton.", exception);
        }
    }

    private String echapper(String valeur) {
        return valeur == null ? "" : valeur.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
