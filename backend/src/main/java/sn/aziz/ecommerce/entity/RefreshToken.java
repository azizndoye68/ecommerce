package sn.aziz.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens", indexes = @Index(name = "idx_refresh_token_hash", columnList = "tokenHash", unique = true))
@Getter @Setter @NoArgsConstructor
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 64) private String tokenHash;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "utilisateur_id") private Utilisateur utilisateur;
    @Column(nullable = false) private LocalDateTime expireLe;
    @Column(nullable = false) private boolean revoque;
    @Column(nullable = false, updatable = false) private LocalDateTime creeLe;
    @PrePersist void prePersist() { creeLe = LocalDateTime.now(); }
}
