package sn.aziz.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.aziz.ecommerce.entity.JetonReinitialisation;
import java.time.LocalDateTime;
import java.util.Optional;

public interface JetonReinitialisationRepository extends JpaRepository<JetonReinitialisation, Long> {
    Optional<JetonReinitialisation> findByJetonHash(String jetonHash);
    long deleteByUtilisateurId(Long utilisateurId);
    long deleteByExpireLeBefore(LocalDateTime date);
}
