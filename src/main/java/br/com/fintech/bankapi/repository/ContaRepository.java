package br.com.fintech.bankapi.repository;

import br.com.fintech.bankapi.entity.Conta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    @Query("select t from Conta t where t.cliente.id = :clienteId")
    Page<Conta> findAllByClienteId(@Param("clienteId") Long clienteId, Pageable pageable);
}
