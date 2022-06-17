package br.com.fintech.bankapi.controller;

import br.com.fintech.bankapi.dto.ContaDTO;
import br.com.fintech.bankapi.entity.Cliente;
import br.com.fintech.bankapi.entity.Conta;
import br.com.fintech.bankapi.service.ClienteService;
import br.com.fintech.bankapi.service.ContaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
@WebMvcTest(ContaController.class)
public class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContaService contaService;

    @MockBean
    private ClienteService clienteService;

    @Value(value = "#{'${bankapi.agencia}'}")
    private String AGENCIA;

    private Cliente cliente;
    private Conta conta;
    private BigDecimal deposito;
    private List<Conta> contas;
    private Page<Conta> contaPage;
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        this.objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        cliente = Cliente.builder().id(1L).nome("teste").contas(new HashSet<>()).build();
        deposito = new BigDecimal(1000);

        conta = Conta.builder().id(1L).conta("0000000001").agencia(this.AGENCIA).saldo(deposito).cliente(cliente).build();


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
    }

    @Test
    public void findById() throws Exception {
        given(this.contaService.findById(any(Long.class))).willReturn(this.conta);
        mockMvc.perform(get("/conta")
                .contentType(MediaType.APPLICATION_JSON)
                .param("contaId", this.conta.getId().toString()))
                .andExpect(jsonPath("$.conta", is(this.conta.getConta())));
    }

    @Test
    public void findAllByClienteId() throws Exception {
        given(this.contaService.findAllByClienteId(any(Long.class), any(), any())).willReturn(this.contaPage);
        mockMvc.perform(get("/conta/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .param("clienteId", this.cliente.getId().toString()))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].conta", is(this.conta.getConta())));
    }

    @Test
    public void create() throws Exception {
        given(clienteService.findById(any(Long.class))).willReturn(this.cliente);
        given(contaService.create(any(Cliente.class), any(BigDecimal.class))).willReturn(this.conta);

        ContaDTO contaDTO = new ContaDTO();
        contaDTO.setClienteId(this.cliente.getId());
        contaDTO.setDeposito(this.deposito);
        String json = objectMapper.writeValueAsString(contaDTO);

        mockMvc.perform(post("/conta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agencia", is(this.AGENCIA)))
                .andExpect(jsonPath("$.conta", is(this.conta.getConta())));
    }

    @Test
    public void getSaldo() throws Exception {
        given(this.contaService.findById(any(Long.class))).willReturn(this.conta);
        mockMvc.perform(get("/conta/saldo")
                .contentType(MediaType.APPLICATION_JSON)
                .param("contaId", this.conta.getId().toString()))
                .andExpect(status().isOk());
    }
}
