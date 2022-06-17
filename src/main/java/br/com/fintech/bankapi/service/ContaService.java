package br.com.fintech.bankapi.service;

import br.com.fintech.bankapi.entity.Cliente;
import br.com.fintech.bankapi.entity.Conta;
import br.com.fintech.bankapi.entity.Movimentacao;
import br.com.fintech.bankapi.exception.NoDataFoundException;
import br.com.fintech.bankapi.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Component
@Service
@RequiredArgsConstructor
public class ContaService {
    @Autowired
    private ContaRepository contaRepository;
    @Autowired
    private MovimentacaoService movimentacaoService;

    @Value(value = "#{'${bankapi.agencia}'}")
    private String AGENCIA;

    public Page<Conta> findAllByClienteId(Long clienteId, Integer skip, Integer take) {
        Page<Conta> page;
        if (take == null || take == 0) {
            page = this.contaRepository.findAllByClienteId(clienteId, Pageable.unpaged());
        } else {
            page = this.contaRepository.findAllByClienteId(clienteId, PageRequest.of((skip / take), take, Sort.by("id").ascending()));
        }
        return page;
    }

    @Transactional
    public Conta create(Cliente cliente, BigDecimal deposito) {
        Conta conta = new Conta();
        conta.setAgencia(this.AGENCIA);
        conta.setCliente(cliente);
        conta.setSaldo(new BigDecimal(0));
        conta.setConta("TEMP");
        Conta save = this.contaRepository.save(conta);

        save.setConta(String.format("%10s", save.getId()).replace(' ', '0'));

        if (deposito != null && deposito.compareTo(new BigDecimal(0)) > 0) {
            Movimentacao m = new Movimentacao();
            this.movimentacaoService.depositar(m, save, deposito);
        }

        return this.update(save);
    }

    @Transactional
    public Conta update(Conta conta) {
        return this.contaRepository.save(conta);
    }

    public Conta findById(Long contaId) {
        try {
            return this.contaRepository.findById(contaId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new NoDataFoundException("Conta não encontrada.");
        }
    }
}
