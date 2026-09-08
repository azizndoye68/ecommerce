package sn.aziz.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.aziz.ecommerce.entity.Panier;
import java.util.Optional;

public interface PanierRepository extends JpaRepository<Panier, Long> {
    Optional<Panier> findByUtilisateurId(Long utilisateurId);
}
