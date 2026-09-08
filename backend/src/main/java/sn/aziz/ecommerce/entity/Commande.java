package sn.aziz.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
@Getter @Setter @NoArgsConstructor
public class Commande {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "utilisateur_id") private Utilisateur utilisateur;
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true) private List<LigneCommande> lignes = new ArrayList<>();
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StatutCommande statut;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ModePaiement modePaiement;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal total;
    @Column(unique = true) private String referencePaiement;
    @Column(length = 1000) private String urlPaiement;
    @Column(nullable = false) private String destinataireLivraison;
    @Column(nullable = false) private String telephoneLivraison;
    @Column(nullable = false, length = 1000) private String adresseLivraison;
    @Column(nullable = false) private String villeLivraison;
    @Column(nullable = false) private String paysLivraison;
    @Column(nullable = false, updatable = false) private LocalDateTime creeLe;
    @PrePersist void prePersist() { creeLe = LocalDateTime.now(); }
    public void ajouterLigne(LigneCommande ligne) { lignes.add(ligne); ligne.setCommande(this); }
}
