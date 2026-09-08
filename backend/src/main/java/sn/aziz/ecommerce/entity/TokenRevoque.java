package sn.aziz.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens_revoques")
@Getter @Setter @NoArgsConstructor
public class TokenRevoque {
    @Id private String jti;
    private LocalDateTime expireLe;
    public TokenRevoque(String jti, LocalDateTime expireLe) { this.jti = jti; this.expireLe = expireLe; }
}
