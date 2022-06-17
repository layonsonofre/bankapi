package br.com.fintech.bankapi.controller;

import br.com.fintech.bankapi.dto.ClienteDTO;
import br.com.fintech.bankapi.entity.Cliente;
import br.com.fintech.bankapi.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;
    private Cliente cliente;
    private List<Cliente> clientes;
    private Page<Cliente> clientePage;
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        this.objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        cliente = Cliente.builder().id(1L).nome("teste").contas(new HashSet<>()).build();

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
    }

    @Test
    public void findAll() throws Exception {
        given(clienteService.findAll(null, null)).willReturn(this.clientePage);
        mockMvc.perform(get("/cliente").contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome", is(this.cliente.getNome())));
    }

    @Test
    public void create() throws Exception {
        given(clienteService.saveCliente(any(Cliente.class), any(BigDecimal.class))).willReturn(this.cliente);
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setCliente(this.cliente);
        clienteDTO.setDeposito(new BigDecimal(10));
        String json = objectMapper.writeValueAsString(clienteDTO);

        mockMvc.perform(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is(this.cliente.getNome())));
    }

    @Test
    public void update() throws Exception {
        given(clienteService.saveCliente(any(Cliente.class), any())).willReturn(this.cliente);
        String json = objectMapper.writeValueAsString(this.cliente);
        mockMvc.perform(put("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is(this.cliente.getNome())));
    }
}
