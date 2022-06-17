package br.com.fintech.bankapi.service;

import br.com.fintech.bankapi.entity.Cliente;
import br.com.fintech.bankapi.entity.Conta;
import br.com.fintech.bankapi.entity.Movimentacao;
import br.com.fintech.bankapi.repository.ContaRepository;
import br.com.fintech.bankapi.repository.MovimentacaoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ContaServiceTest {
    @Autowired
    private ContaService contaService;
    @MockBean
    private ContaRepository contaRepository;
    @MockBean
    private MovimentacaoRepository movimentacaoRepository;

    private Conta conta;
    private List<Conta> contas;
    private Page<Conta> contaPage;
    private Cliente cliente;
    private BigDecimal deposito;
    private Movimentacao movimentacao;

    @Value(value = "#{'${bankapi.agencia}'}")
    private String AGENCIA;

    @Before
    public void setup() {
        cliente = Cliente.builder().id(1L).nome("teste").contas(new HashSet<>()).build();
        deposito = new BigDecimal(1000);

        conta = Conta.builder().id(1L).conta("0000000001").agencia(this.AGENCIA).saldo(deposito).build();
        movimentacao = Movimentacao.builder().id(1L).operacao(1).valor(deposito).data(LocalDateTime.now()).conta(conta).build();
        Set<Movimentacao> movimentacoes = new HashSet<>();
        movimentacoes.add(movimentacao);
        conta.setMovimentacoes(movimentacoes);

        Set<Conta> hashConta = new HashSet<>();
        hashConta.add(conta);
        cliente.setContas(hashConta);

        contas = new ArrayList<>();
        contas.add(conta);

        contaPage = new Page<Conta>() {
            @Override
            public int getTotalPages() {
                return 1;
            }

            @Override
            public long getTotalElements() {
                return 1;
            }

            @Override
            public <U> Page<U> map(Function<? super Conta, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public int getNumberOfElements() {
                return 1;
            }

            @Override
            public List<Conta> getContent() {
                return contas;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<Conta> iterator() {
                return null;
            }
        };

        Mockito.when(contaRepository.findAllByClienteId(this.cliente.getId(), Pageable.unpaged())).thenReturn(contaPage);
        Mockito.when(contaRepository.findById(conta.getId())).thenReturn(Optional.of(conta));
        Mockito.when(contaRepository.save(conta)).thenReturn(conta);
        Mockito.when(contaRepository.saveAndFlush(conta)).thenReturn(conta);
        Mockito.when(movimentacaoRepository.save(movimentacao)).thenReturn(movimentacao);
    }

    @Test
    @DisplayName("Lista as contas de um cliente")
    public void findAllByClienteId() {
        Page<Conta> page = this.contaService.findAllByClienteId(this.cliente.getId(), null, null);
        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
    }

    @DisplayName("Cria conta com depósito inicial")
    public void createDeposito() {
        Conta conta = this.contaService.create(this.cliente, this.deposito);
        assertNotNull(conta);
        assertEquals(conta.getConta(), this.conta.getConta());
        assertEquals(conta.getSaldo(), this.deposito);
    }

    @Test
    @DisplayName("Atualiza informações da conta")
    public void update() {
        Conta conta = this.contaService.update(this.conta);
        assertNotNull(conta);
        assertEquals(conta.getConta(), this.conta.getConta());
    }

    @Test
    @DisplayName("Detalhes da conta")
    public void findById() {
        Conta conta = this.contaService.findById(this.conta.getId());
        assertNotNull(conta);
        assertEquals(conta.getConta(), this.conta.getConta());
    }
}
