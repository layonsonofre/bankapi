package br.com.fintech.bankapi.service;

import br.com.fintech.bankapi.entity.Conta;
import br.com.fintech.bankapi.entity.Movimentacao;
import br.com.fintech.bankapi.exception.CustomException;
import br.com.fintech.bankapi.repository.ContaRepository;
import br.com.fintech.bankapi.repository.MovimentacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Service
@RequiredArgsConstructor
public class MovimentacaoService {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    @Autowired
    private ContaRepository contaRepository;


    @Value(value = "#{'${bankapi.op_deposito}'}")
    private Integer OP_DEPOSITO;

    @Value(value = "#{'${bankapi.op_saque}'}")
    private Integer OP_SAQUE;

    @Value(value = "#{'${bankapi.op_transferencia_envio}'}")
    private Integer OP_TRANSFERENCIA_ENVIO;

    @Value(value = "#{'${bankapi.op_transferencia_recebimento}'}")
    private Integer OP_TRANSFERENCIA_RECEBIMENTO;

    public Page<Movimentacao> findAllByContaId(Long contaId, Integer skip, Integer take) {
        Page<Movimentacao> page;
        if (take == null || take == 0) {
            page = this.movimentacaoRepository.findAllByContaId(contaId, Pageable.unpaged());
        } else {
            page = this.movimentacaoRepository.findAllByContaId(contaId, PageRequest.of((skip / take), take, Sort.by("data").ascending()));
        }
        return page;
    }

    @Transactional
    public Movimentacao depositar(Movimentacao movimentacao, Conta conta, BigDecimal valor) {
        if (valor == null || valor.compareTo(new BigDecimal(0)) <= 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Necessário informar um valor de depósito.");
        }

        movimentacao.setConta(conta);
        movimentacao.setValor(valor);
        movimentacao.setOperacao(OP_DEPOSITO);
        Movimentacao ret = this.movimentacaoRepository.save(movimentacao);
        conta.setSaldo(conta.getSaldo().add(valor));
        this.contaRepository.save(conta);

        return ret;
    }

    @Transactional
    public Movimentacao sacar(Movimentacao movimentacao, Conta conta, BigDecimal valor) {
        if (valor == null || valor.compareTo(new BigDecimal(0)) <= 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Necessário informar um valor para sacar.");
        }

        if (conta.getSaldo().compareTo(valor) <= 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "A conta não possui saldo suficiente para realizar este saque.");
        }

        movimentacao.setConta(conta);
        movimentacao.setValor(valor);
        movimentacao.setOperacao(OP_SAQUE);
        Movimentacao ret = this.movimentacaoRepository.save(movimentacao);

        conta.setSaldo(conta.getSaldo().subtract(valor));
        this.contaRepository.save(conta);

        return ret;
    }

    @Transactional
    public Movimentacao transferir(Conta origem, Movimentacao envio, Conta destino, Movimentacao recebimento, BigDecimal valor) {
        if (valor == null || valor.compareTo(new BigDecimal(0)) <= 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Necessário informar um valor para transferir.");
        }

        if (origem.getSaldo().compareTo(valor) < 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para realizar a transferência.");
        }

        envio.setConta(origem);
        envio.setValor(valor);
        envio.setOperacao(OP_TRANSFERENCIA_ENVIO);
        Movimentacao transferencia = this.movimentacaoRepository.save(envio);

        origem.setSaldo(origem.getSaldo().subtract(valor));
        this.contaRepository.save(origem);

        recebimento.setConta(destino);
        recebimento.setValor(valor);
        recebimento.setOperacao(OP_TRANSFERENCIA_RECEBIMENTO);
        this.movimentacaoRepository.save(recebimento);

        destino.setSaldo(destino.getSaldo().add(valor));
        this.contaRepository.save(destino);

        return transferencia;
    }
}
