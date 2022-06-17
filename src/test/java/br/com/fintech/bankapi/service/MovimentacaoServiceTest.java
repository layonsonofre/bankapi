package br.com.fintech.bankapi.service;

import br.com.fintech.bankapi.entity.Cliente;
import br.com.fintech.bankapi.entity.Conta;
import br.com.fintech.bankapi.entity.Movimentacao;
import br.com.fintech.bankapi.exception.CustomException;
import br.com.fintech.bankapi.repository.ContaRepository;
import br.com.fintech.bankapi.repository.MovimentacaoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
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
public class MovimentacaoServiceTest {
    @Autowired
    private MovimentacaoService movimentacaoService;
    @MockBean
    private MovimentacaoRepository movimentacaoRepository;
    @MockBean
    private ContaRepository contaRepository;

    private Cliente cliente;
    private List<Movimentacao> movimentacoesList;
    private Page<Movimentacao> movimentacaoPage;
    private BigDecimal deposito;
    private Conta conta;
    private Conta origem;
    private Conta destino;
    @InjectMocks
    @Spy
    private Movimentacao movimentacao;
    private Movimentacao movimentacaoMock;

    @Value(value = "#{'${bankapi.agencia}'}")
    private String AGENCIA;
    @Value(value = "#{'${bankapi.op_deposito}'}")
    private Integer OP_DEPOSITO;
    @Value(value = "#{'${bankapi.op_transferencia_envio}'}")
    private Integer OP_TRANSFERENCIA_ENVIO;
    @Value(value = "#{'${bankapi.op_transferencia_recebimento}'}")
    private Integer OP_TRANSFERENCIA_RECEBIMENTO;

    @Before
    public void setup() {

        cliente = Cliente.builder().id(1L).nome("teste").contas(new HashSet<>()).build();
        deposito = new BigDecimal(1000);

        conta = Conta.builder().id(1L).conta("0000000001").agencia(this.AGENCIA).saldo(deposito).build();
        movimentacao = Movimentacao.builder().id(1L).operacao(this.OP_DEPOSITO).valor(deposito).data(LocalDateTime.now()).conta(conta).build();
        Set<Movimentacao> movimentacoes = new HashSet<>();
        movimentacoes.add(movimentacao);
        conta.setMovimentacoes(movimentacoes);

        Set<Conta> hashConta = new HashSet<>();
        hashConta.add(conta);
        cliente.setContas(hashConta);

        movimentacoesList = new ArrayList<>();
        movimentacoesList.add(movimentacao);
        movimentacoes.add(movimentacao);


        origem = Conta.builder().id(2L).conta("0000000002").agencia(this.AGENCIA).saldo(deposito).build();
        Movimentacao m = Movimentacao.builder().id(2L).operacao(this.OP_TRANSFERENCIA_ENVIO).valor(deposito).data(LocalDateTime.now()).conta(origem).build();
        movimentacoes = new HashSet<>();
        movimentacoes.add(m);
        origem.setMovimentacoes(movimentacoes);

        destino = Conta.builder().id(3L).conta("0000000003").agencia(this.AGENCIA).saldo(deposito).build();
        m = Movimentacao.builder().id(3L).operacao(this.OP_TRANSFERENCIA_RECEBIMENTO).valor(deposito).data(LocalDateTime.now()).conta(destino).build();
        movimentacoes = new HashSet<>();
        movimentacoes.add(m);
        destino.setMovimentacoes(movimentacoes);

        movimentacaoPage = new Page<Movimentacao>() {
            @Override
            public int getTotalPages() {
                return 1;
            }

            @Override
            public long getTotalElements() {
                return 1;
            }

            @Override
            public <U> Page<U> map(Function<? super Movimentacao, ? extends U> converter) {
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
            public List<Movimentacao> getContent() {
                return movimentacoesList;
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
            public Iterator<Movimentacao> iterator() {
                return null;
            }
        };

        Mockito.when(movimentacaoRepository.findAllByContaId(this.conta.getId(), Pageable.unpaged())).thenReturn(movimentacaoPage);
        Mockito.when(movimentacaoRepository.save(this.movimentacao)).thenReturn(movimentacao);

        Mockito.when(contaRepository.save(conta)).thenReturn(conta);
        Mockito.when(contaRepository.save(origem)).thenReturn(origem);
        Mockito.when(contaRepository.save(destino)).thenReturn(destino);
    }

    @Test
    @DisplayName("Listar movimentações da conta")
    public void findAllByContaId() {
        Page<Movimentacao> page = this.movimentacaoService.findAllByContaId(this.conta.getId(), null, null);
        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    @DisplayName("Depositar")
    public void depositar() throws Exception {
        Movimentacao m = new Movimentacao();
        BigDecimal after = this.conta.getSaldo().add(this.deposito);
        m.setValor(after);
        Mockito.when(movimentacaoRepository.save(m)).thenReturn(m);

        Movimentacao t = this.movimentacaoService.depositar(m, this.conta, this.deposito);
        assertNotNull(t);
        assertEquals(t.getValor(), this.deposito);
        assertEquals(this.conta.getSaldo(), after);
    }

    @Test(expected = CustomException.class)
    @DisplayName("Sacar valor maior que o saldo")
    public void sacarMaior() {
        Movimentacao m = new Movimentacao();
        Movimentacao t = this.movimentacaoService.sacar(m, this.conta, this.deposito.multiply(new BigDecimal(2)));
    }

    @Test
    @DisplayName("Sacar metade do valor")
    public void sacarMetade() {
        BigDecimal half = this.deposito.divide(new BigDecimal(2));

        Movimentacao m = new Movimentacao();
        m.setValor(half);
        Mockito.when(movimentacaoRepository.save(m)).thenReturn(m);
        Movimentacao t = this.movimentacaoService.sacar(m, this.conta, half);
        assertNotNull(t);
        assertEquals(t.getValor(), half);
        assertEquals(this.conta.getSaldo(), half);
    }

    @Test
    @DisplayName("Transferir valores")
    public void transferir() {
        Movimentacao envio = new Movimentacao();
        BigDecimal saldoOrigem = this.origem.getSaldo().subtract(this.deposito);
        Mockito.when(movimentacaoRepository.save(envio)).thenReturn(envio);

        Movimentacao recebimento = new Movimentacao();
        BigDecimal saldoDestino = this.destino.getSaldo().add(this.deposito);
        Mockito.when(movimentacaoRepository.save(recebimento)).thenReturn(recebimento);

        Movimentacao t = this.movimentacaoService.transferir(origem, envio, destino, recebimento, this.deposito);
        assertNotNull(t);
        assertEquals(t.getValor(), envio.getValor());
        assertEquals(this.origem.getSaldo(), saldoOrigem);
        assertEquals(this.destino.getSaldo(), saldoDestino);
    }
}
