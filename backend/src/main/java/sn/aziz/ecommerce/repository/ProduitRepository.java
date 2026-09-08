package sn.aziz.ecommerce.repository;

import sn.aziz.ecommerce.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    java.util.Optional<Produit> findWithLockById(Long id);

    List<Produit> findByCategorieId(Long categorieId);

    List<Produit> findByNomContainingIgnoreCase(String nom);
}
