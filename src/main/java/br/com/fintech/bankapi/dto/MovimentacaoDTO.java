package br.com.fintech.bankapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MovimentacaoDTO {
    @JsonAlias(value = "conta_id")
    Long contaId;
    BigDecimal valor;
}
