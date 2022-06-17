package br.com.fintech.bankapi.service;

import br.com.fintech.bankapi.entity.Cliente;
import br.com.fintech.bankapi.entity.Conta;
import br.com.fintech.bankapi.entity.Movimentacao;
import br.com.fintech.bankapi.repository.ClienteRepository;
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
public class ClienteServiceTest {
    private Cliente cliente;
    private List<Cliente> clientes;
    private Page<Cliente> clientePage;
    private BigDecimal deposito;
    private Conta conta;
    private Movimentacao movimentacao;

    @Autowired
    private ClienteService clienteService;
    @MockBean
    private ClienteRepository clienteRepository;
    @MockBean
    private ContaRepository contaRepository;
    @MockBean
    private MovimentacaoRepository movimentacaoRepository;

    @Value(value = "#{'${bankapi.agencia}'}")
    private String AGENCIA;
    @Value(value = "#{'${bankapi.op_deposito}'}")
    private Integer OP_DEPOSITO;

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

        clientes = new ArrayList<>();
        clientes.add(cliente);

        clientePage = new Page<Cliente>() {
            @Override
            public int getTotalPages() {
                return 1;
            }

            @Override
            public long getTotalElements() {
                return 1;
            }

            @Override
            public <U> Page<U> map(Function<? super Cliente, ? extends U> converter) {
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
            public List<Cliente> getContent() {
                return clientes;
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
            public Iterator<Cliente> iterator() {
                return null;
            }
        };

        Mockito.when(clienteRepository.findAll(Pageable.unpaged())).thenReturn(clientePage);
        Mockito.when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        Mockito.when(clienteRepository.saveAndFlush(cliente)).thenReturn(cliente);
        Mockito.when(contaRepository.save(conta)).thenReturn(conta);
        Mockito.when(movimentacaoRepository.save(movimentacao)).thenReturn(movimentacao);
    }

    @Test
    @DisplayName("Listar clientes")
    public void findAll() {
        Page<Cliente> found = clienteService.findAll(null, null);
        assertNotNull(found);
        assertEquals(1, found.getTotalElements());
    }

    @Test
    @DisplayName("Detalhes de um cliente")
    public void findById() {
        Cliente found = clienteService.findById(this.cliente.getId());
        assertNotNull(found);
        assertEquals(found.getNome(), this.cliente.getNome());
    }

    @Test
    @DisplayName("Cria cliente sem depósito inicial")
    public void saveCliente() {
        Cliente c = clienteService.saveCliente(cliente, null);
        assertNotNull(c);
        assertEquals(c.getNome(), cliente.getNome());
        assertEquals(c.getContas().size(), cliente.getContas().size());
    }

    @Test
    @DisplayName("Cria cliente com depósito inicial, criando uma conta")
    public void saveClienteDeposito() {
        Cliente c = clienteService.saveCliente(cliente, this.deposito);
        assertNotNull(c);
        assertEquals(c.getNome(), cliente.getNome());
        assertEquals(c.getContas().size(), 1);
        Conta conta = c.getContas().stream().findFirst().orElseThrow();
        assertEquals(conta.getConta(), this.conta.getConta());
        assertEquals(conta.getAgencia(), this.conta.getAgencia());
        assertEquals(conta.getSaldo(), this.deposito);
    }
}
