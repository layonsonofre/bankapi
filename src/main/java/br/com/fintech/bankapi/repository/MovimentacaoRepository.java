package br.com.fintech.bankapi.repository;

import br.com.fintech.bankapi.entity.Movimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    @Query("select t from Movimentacao t where t.conta.id = :contaId")
    Page<Movimentacao> findAllByContaId(@Param("contaId") Long contaId, Pageable pageable);
}
