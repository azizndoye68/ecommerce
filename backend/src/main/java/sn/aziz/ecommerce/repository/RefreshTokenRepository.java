package sn.aziz.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sn.aziz.ecommerce.entity.RefreshToken;
import java.util.Optional;
import java.time.LocalDateTime;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    @Modifying
    @Query("update RefreshToken r set r.revoque = true where r.id = :id and r.revoque = false and r.expireLe > :maintenant")
    int revoquerSiActif(@Param("id") Long id, @Param("maintenant") LocalDateTime maintenant);
    long deleteByExpireLeBefore(LocalDateTime date);
    long deleteByUtilisateurId(Long utilisateurId);
}
