package br.com.fintech.bankapi.controller;

import br.com.fintech.bankapi.dto.MovimentacaoDTO;
import br.com.fintech.bankapi.dto.TransferenciaDTO;
import br.com.fintech.bankapi.entity.Conta;
import br.com.fintech.bankapi.entity.Movimentacao;
import br.com.fintech.bankapi.service.ContaService;
import br.com.fintech.bankapi.service.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movimentacao")
public class MovimentacaoController {
    @Autowired
    private MovimentacaoService movimentacaoService;
    @Autowired
    private ContaService contaService;

    @GetMapping
    public Page<Movimentacao> findAllByContaId(@RequestParam Long contaId, @RequestParam(required = false) Integer skip, @RequestParam(required = false) Integer take) {
        return this.movimentacaoService.findAllByContaId(contaId, skip, take);
    }

    @PostMapping(value = "/depositar")
    public Movimentacao depositar(@RequestBody MovimentacaoDTO movimentacaoDTO) {
        Conta conta = this.contaService.findById(movimentacaoDTO.getContaId());
        Movimentacao m = new Movimentacao();
        return this.movimentacaoService.depositar(m, conta, movimentacaoDTO.getValor());
    }

    @PostMapping(value = "/sacar")
    public Movimentacao sacar(@RequestBody MovimentacaoDTO movimentacaoDTO) {
        Conta conta = this.contaService.findById(movimentacaoDTO.getContaId());
        Movimentacao m = new Movimentacao();
        return this.movimentacaoService.sacar(m, conta, movimentacaoDTO.getValor());
    }

    @PostMapping(value = "/transferir")
    public Movimentacao transferir(@RequestBody TransferenciaDTO transferenciaDTO) {
        Conta origem = this.contaService.findById(transferenciaDTO.getOrigemContaId());
        Conta destino = this.contaService.findById(transferenciaDTO.getDestinoContaId());
        Movimentacao envio = new Movimentacao();
        Movimentacao recebimento = new Movimentacao();
        return this.movimentacaoService.transferir(origem, envio, destino, recebimento, transferenciaDTO.getValor());
    }
}
