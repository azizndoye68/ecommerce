package sn.aziz.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_commande")
@Getter @Setter @NoArgsConstructor
public class LigneCommande {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "commande_id") private Commande commande;
    @Column(nullable = false) private Long produitId;
    @Column(nullable = false) private String nomProduit;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal prixUnitaire;
    @Column(nullable = false) private Integer quantite;
}
