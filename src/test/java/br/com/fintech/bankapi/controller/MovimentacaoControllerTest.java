package br.com.fintech.bankapi.controller;


import br.com.fintech.bankapi.dto.MovimentacaoDTO;
import br.com.fintech.bankapi.dto.TransferenciaDTO;
import br.com.fintech.bankapi.entity.Cliente;
import br.com.fintech.bankapi.entity.Conta;
import br.com.fintech.bankapi.entity.Movimentacao;
import br.com.fintech.bankapi.service.ContaService;
import br.com.fintech.bankapi.service.MovimentacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MovimentacaoController.class)
public class MovimentacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MovimentacaoService movimentacaoService;
    @MockBean
    private ContaService contaService;

    private ObjectMapper objectMapper;

    @Value(value = "#{'${bankapi.agencia}'}")
    private String AGENCIA;
    @Value(value = "#{'${bankapi.op_deposito}'}")
    private Integer OP_DEPOSITO;
    @Value(value = "#{'${bankapi.op_saque}'}")
    private Integer OP_SAQUE;
    @Value(value = "#{'${bankapi.op_transferencia_envio}'}")
    private Integer OP_TRANSFERENCIA_ENVIO;
    @Value(value = "#{'${bankapi.op_transferencia_recebimento}'}")
    private Integer OP_TRANSFERENCIA_RECEBIMENTO;


    private Cliente cliente;
    private List<Movimentacao> movimentacoesList;
    private Page<Movimentacao> movimentacaoPage;
    private BigDecimal deposito;
    private Conta conta;
    private Conta origem;
    private Conta destino;
    private Movimentacao movimentacao;

    @Before
    public void setup() {
        this.objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

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
    }

    @Test
    public void findAllByContaId() throws Exception {
        given(this.movimentacaoService.findAllByContaId(any(), any(), any())).willReturn(this.movimentacaoPage);
        mockMvc.perform(get("/movimentacao")
                .contentType(MediaType.APPLICATION_JSON)
                .param("contaId", this.movimentacao.getConta().getId().toString()))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].operacao", is(this.movimentacao.getOperacao())));
    }

    @Test
    public void depositar() throws Exception{
        given(this.movimentacaoService.depositar(any(), any(), any())).willReturn(this.movimentacao);

        MovimentacaoDTO movimentacaoDTO = new MovimentacaoDTO();
        movimentacaoDTO.setContaId(this.conta.getId());
        movimentacaoDTO.setValor(this.deposito);
        String json = objectMapper.writeValueAsString(movimentacaoDTO);

        mockMvc.perform(post("/movimentacao/depositar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(jsonPath("$.operacao", is(this.OP_DEPOSITO)));
    }

    @Test
    public void sacar() throws Exception {
        Movimentacao saque = new Movimentacao();
        saque.setConta(this.conta);
        saque.setOperacao(this.OP_SAQUE);
        given(this.movimentacaoService.sacar(any(), any(), any())).willReturn(saque);

        MovimentacaoDTO movimentacaoDTO = new MovimentacaoDTO();
        movimentacaoDTO.setContaId(this.conta.getId());
        movimentacaoDTO.setValor(this.deposito);
        String json = objectMapper.writeValueAsString(movimentacaoDTO);

        mockMvc.perform(post("/movimentacao/sacar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(jsonPath("$.operacao", is(this.OP_SAQUE)));
    }

    @Test
    public void transferir() throws Exception {
        Movimentacao envio = new Movimentacao();
        envio.setConta(this.conta);
        envio.setOperacao(this.OP_TRANSFERENCIA_ENVIO);
        given(this.movimentacaoService.transferir(any(), any(), any(), any(), any())).willReturn(envio);

        TransferenciaDTO transferenciaDTO = new TransferenciaDTO();
        transferenciaDTO.setValor(this.deposito);
        transferenciaDTO.setDestinoContaId(this.destino.getId());
        transferenciaDTO.setOrigemContaId(this.origem.getId());
        String json = objectMapper.writeValueAsString(transferenciaDTO);

        mockMvc.perform(post("/movimentacao/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(jsonPath("$.operacao", is(this.OP_TRANSFERENCIA_ENVIO)));
    }

}
