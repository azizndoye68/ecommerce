package sn.aziz.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_audit", indexes = @Index(name = "idx_audit_cree_le", columnList = "creeLe"))
@Getter @Setter @NoArgsConstructor
public class JournalAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long acteurId;
    @Column(nullable = false) private String action;
    @Column(nullable = false) private String ressource;
    @Column(nullable = false, updatable = false) private LocalDateTime creeLe;
    public JournalAudit(Long acteurId, String action, String ressource) { this.acteurId = acteurId; this.action = action; this.ressource = ressource; }
    @PrePersist void prePersist() { creeLe = LocalDateTime.now(); }
}
