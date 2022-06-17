package br.com.fintech.bankapi.dto;

import br.com.fintech.bankapi.entity.Cliente;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ClienteDTO {
    Cliente cliente;
    BigDecimal deposito;
}
