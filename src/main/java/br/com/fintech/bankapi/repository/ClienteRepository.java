package br.com.fintech.bankapi.repository;

import br.com.fintech.bankapi.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}
