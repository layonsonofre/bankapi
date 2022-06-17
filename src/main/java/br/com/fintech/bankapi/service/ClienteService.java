package br.com.fintech.bankapi.service;

import br.com.fintech.bankapi.entity.Cliente;
import br.com.fintech.bankapi.entity.Conta;
import br.com.fintech.bankapi.exception.NoDataFoundException;
import br.com.fintech.bankapi.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ContaService contaService;

    public Page<Cliente> findAll(Integer skip, Integer take) {
        Page<Cliente> page;
        if (take == null || take == 0) {
            page = this.clienteRepository.findAll(Pageable.unpaged());
        } else {
            page = this.clienteRepository.findAll(PageRequest.of((skip / take), take, Sort.by("nome").ascending()));
        }
        return page;
    }

    @Transactional
    public Cliente saveCliente(Cliente cliente, BigDecimal deposito) {
        Long currentId = cliente.getId();
        Cliente ret = this.clienteRepository.saveAndFlush(cliente);

        if (currentId == null) {
            Conta conta = this.contaService.create(ret, deposito);
        }

        return ret;
    }

    public Cliente findById(Long clienteId) {
        try {
            return this.clienteRepository.findById(clienteId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new NoDataFoundException("Cliente não encontrado.");
        }
    }
}
