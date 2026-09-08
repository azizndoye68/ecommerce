package sn.aziz.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.aziz.ecommerce.entity.Commande;
import java.util.List;
import java.util.Optional;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findByUtilisateurIdOrderByCreeLeDesc(Long utilisateurId);
    Optional<Commande> findByIdAndUtilisateurId(Long id, Long utilisateurId);
    Optional<Commande> findByReferencePaiement(String referencePaiement);
}
