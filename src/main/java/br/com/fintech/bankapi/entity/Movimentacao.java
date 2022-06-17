package br.com.fintech.bankapi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "movimentacao")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movimentacao {
    @Id
    @Column(name = "movimentacao_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToOne()
    @JoinColumn(name = "conta_id", nullable = false)
    @JsonIgnore
    Conta conta;
    @Column(name = "valor", nullable = false)
    BigDecimal valor;
    @Column(name = "operacao", nullable = false)
    Integer operacao;
    @Column(name = "data")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime data;

    @Override
    public String toString() {
        return "Movimentacao{" +
                "id=" + id +
                ", conta=" + conta +
                ", valor=" + valor +
                ", operacao=" + operacao +
                ", data=" + data +
                '}';
    }
}
