package sn.aziz.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.aziz.ecommerce.entity.RefreshToken;
import java.util.Optional;
import java.time.LocalDateTime;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    long deleteByExpireLeBefore(LocalDateTime date);
}
