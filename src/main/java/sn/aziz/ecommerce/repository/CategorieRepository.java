package sn.aziz.ecommerce.repository;

import sn.aziz.ecommerce.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    Optional<Categorie> findByNom(String nom);

    boolean existsByNom(String nom);
}