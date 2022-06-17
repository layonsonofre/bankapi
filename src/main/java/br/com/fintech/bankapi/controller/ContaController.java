package br.com.fintech.bankapi.controller;

import br.com.fintech.bankapi.dto.ContaDTO;
import br.com.fintech.bankapi.entity.Cliente;
import br.com.fintech.bankapi.entity.Conta;
import br.com.fintech.bankapi.service.ClienteService;
import br.com.fintech.bankapi.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/conta")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public Conta findById(@RequestParam Long contaId) {
        return this.contaService.findById(contaId);
    }

    @GetMapping(value = "/cliente")
    public Page<Conta> findAllByClienteId(@RequestParam Long clienteId, @RequestParam(required = false) Integer skip, @RequestParam(required = false) Integer take) {
        return this.contaService.findAllByClienteId(clienteId, skip, take);
    }

    @PostMapping
    public Conta create(@RequestBody ContaDTO contaDTO) {
        Cliente cliente = clienteService.findById(contaDTO.getClienteId());
        return this.contaService.create(cliente, contaDTO.getDeposito());
    }

    @GetMapping(value = "/saldo")
    public BigDecimal getSaldo(@RequestParam Long contaId) {
        Conta conta = this.contaService.findById(contaId);
        return conta.getSaldo();
    }
}
