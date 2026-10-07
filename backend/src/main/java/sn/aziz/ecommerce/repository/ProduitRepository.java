package sn.aziz.ecommerce.repository;

import sn.aziz.ecommerce.entity.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Produit> findWithLockById(Long id);

    Page<Produit> findByCategorieId(Long categorieId, Pageable pageable);

    Page<Produit> findByNomContainingIgnoreCase(String nom, Pageable pageable);
}