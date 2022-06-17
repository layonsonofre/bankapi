package br.com.fintech.bankapi.controller;

import br.com.fintech.bankapi.dto.ClienteDTO;
import br.com.fintech.bankapi.entity.Cliente;
import br.com.fintech.bankapi.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public Page<Cliente> findAll(@RequestParam(required = false) Integer skip, @RequestParam(required = false) Integer take) {
        return this.clienteService.findAll(skip, take);
    }

    @PostMapping
    public Cliente create(@RequestBody ClienteDTO clienteDTO) {
        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getCliente().getNome());
        return this.clienteService.saveCliente(cliente, clienteDTO.getDeposito());
    }

    @PutMapping
    public Cliente update(@RequestBody Cliente cliente) {
        return this.clienteService.saveCliente(cliente, null);
    }
}
