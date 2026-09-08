package sn.aziz.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import sn.aziz.ecommerce.entity.RefreshToken;
import sn.aziz.ecommerce.entity.Utilisateur;
import sn.aziz.ecommerce.repository.RefreshTokenRepository;
import sn.aziz.ecommerce.repository.TokenRevoqueRepository;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private TokenRevoqueRepository tokenRevoqueRepository;
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService(refreshTokenRepository, tokenRevoqueRepository);
        ReflectionTestUtils.setField(sessionService, "refreshExpiration", 60_000L);
    }

    @Test
    void creeUnRefreshTokenDontSeulLeHashEstPersisté() {
        String token = sessionService.creerRefreshToken(new Utilisateur());

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(token).contains(".");
        assertThat(captor.getValue().getTokenHash()).isEqualTo(hash(token));
        assertThat(captor.getValue().getTokenHash()).doesNotContain(token);
        assertThat(captor.getValue().getExpireLe()).isAfter(LocalDateTime.now().plusSeconds(50));
    }

    @Test
    void consommerUnRefreshTokenLeRevoque() {
        String token = "refresh-token";
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setActif(true);
        RefreshToken refresh = new RefreshToken();
        refresh.setUtilisateur(utilisateur);
        refresh.setExpireLe(LocalDateTime.now().plusMinutes(1));
        refresh.setRevoque(false);
        when(refreshTokenRepository.findByTokenHash(hash(token))).thenReturn(Optional.of(refresh));

        assertThat(sessionService.consommerRefreshToken(token)).isSameAs(utilisateur);
        assertThat(refresh.isRevoque()).isTrue();
    }

    @Test
    void refuseUnRefreshTokenDejaRevoque() {
        RefreshToken refresh = new RefreshToken();
        refresh.setRevoque(true);
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(refresh));

        assertThatThrownBy(() -> sessionService.consommerRefreshToken("ancien"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void purgeLesSessionsEtJetonsRevoquesExpires() {
        sessionService.purgerSessionsExpirees();

        verify(refreshTokenRepository).deleteByExpireLeBefore(any());
        verify(tokenRevoqueRepository).deleteByExpireLeBefore(any());
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }
}
