package sn.aziz.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import sn.aziz.ecommerce.entity.RefreshToken;
import sn.aziz.ecommerce.entity.TokenRevoque;
import sn.aziz.ecommerce.entity.Utilisateur;
import sn.aziz.ecommerce.exception.RessourceNonTrouveeException;
import sn.aziz.ecommerce.repository.RefreshTokenRepository;
import sn.aziz.ecommerce.repository.TokenRevoqueRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenRevoqueRepository tokenRevoqueRepository;
    @Value("${jwt.refresh-expiration}") private long refreshExpiration;

    public String creerRefreshToken(Utilisateur utilisateur) {
        String token = UUID.randomUUID() + "." + UUID.randomUUID();
        RefreshToken refresh = new RefreshToken();
        refresh.setTokenHash(hash(token)); refresh.setUtilisateur(utilisateur);
        refresh.setExpireLe(LocalDateTime.now().plusSeconds(refreshExpiration / 1000));
        refreshTokenRepository.save(refresh);
        return token;
    }

    public Utilisateur consommerRefreshToken(String token) {
        RefreshToken refresh = refreshTokenRepository.findByTokenHash(hash(token))
                .orElseThrow(() -> new IllegalArgumentException("Session invalide"));
        if (refresh.isRevoque() || refresh.getExpireLe().isBefore(LocalDateTime.now()) || !refresh.getUtilisateur().isEnabled())
            throw new IllegalArgumentException("Session expirée ou révoquée");
        refresh.setRevoque(true);
        return refresh.getUtilisateur();
    }

    public void revoquerAccessToken(String jti, LocalDateTime expiration) {
        if (jti != null && expiration != null && expiration.isAfter(LocalDateTime.now()) && !tokenRevoqueRepository.existsById(jti))
            tokenRevoqueRepository.save(new TokenRevoque(jti, expiration));
    }

    public boolean estRevoque(String jti) { return jti != null && tokenRevoqueRepository.existsById(jti); }

    @Scheduled(fixedDelayString = "${security.session-cleanup-ms:21600000}")
    public void purgerSessionsExpirees() {
        LocalDateTime maintenant = LocalDateTime.now();
        refreshTokenRepository.deleteByExpireLeBefore(maintenant);
        tokenRevoqueRepository.deleteByExpireLeBefore(maintenant);
    }

    private String hash(String valeur) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(valeur.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception ex) { throw new IllegalStateException("Algorithme de hachage indisponible", ex); }
    }
}
