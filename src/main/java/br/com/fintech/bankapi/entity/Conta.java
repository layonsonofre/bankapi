package br.com.fintech.bankapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Table(name = "conta")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conta {
    @Id
    @Column(name = "conta_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToOne()
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnore
    Cliente cliente;
    @Column(name = "agencia", nullable = false)
    String agencia;
    @Column(name = "conta", nullable = false)
    String conta;
    @Column(name = "saldo", nullable = false)
    BigDecimal saldo;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    Set<Movimentacao> movimentacoes = new HashSet<>();

    @Override
    public String toString() {
        return "Conta{" +
                "id=" + id +
                ", cliente=" + cliente +
                ", agencia='" + agencia + '\'' +
                ", conta='" + conta + '\'' +
                ", saldo=" + saldo +
                '}';
    }
}
