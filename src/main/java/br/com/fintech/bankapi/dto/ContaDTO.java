package br.com.fintech.bankapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ContaDTO {
    Long clienteId;
    BigDecimal deposito;
}
