package sn.aziz.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paniers")
@Getter @Setter @NoArgsConstructor
public class Panier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LignePanier> lignes = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime creeLe;
    @Column(nullable = false)
    private LocalDateTime modifieLe;

    @PrePersist void prePersist() { creeLe = modifieLe = LocalDateTime.now(); }
    @PreUpdate void preUpdate() { modifieLe = LocalDateTime.now(); }

    public void ajouterLigne(LignePanier ligne) { lignes.add(ligne); ligne.setPanier(this); }
    public void retirerLigne(LignePanier ligne) { lignes.remove(ligne); ligne.setPanier(null); }
}
