package sn.aziz.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lignes_panier", uniqueConstraints = @UniqueConstraint(columnNames = {"panier_id", "produit_id"}))
@Getter @Setter @NoArgsConstructor
public class LignePanier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "panier_id")
    private Panier panier;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "produit_id")
    private Produit produit;
    @Column(nullable = false) private Integer quantite;
}
