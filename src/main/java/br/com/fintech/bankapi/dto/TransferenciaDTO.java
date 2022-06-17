package br.com.fintech.bankapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferenciaDTO {
    @JsonAlias(value = "origem_conta_id")
    Long origemContaId;
    @JsonAlias(value = "destino_conta_id")
    Long destinoContaId;
    BigDecimal valor;
}
