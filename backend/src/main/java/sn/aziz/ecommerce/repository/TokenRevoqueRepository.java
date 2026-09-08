package sn.aziz.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.aziz.ecommerce.entity.TokenRevoque;
import java.time.LocalDateTime;

public interface TokenRevoqueRepository extends JpaRepository<TokenRevoque, String> {
    long deleteByExpireLeBefore(LocalDateTime date);
}
