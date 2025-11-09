package com.produto.oficina.repository;

import com.produto.oficina.model.OrdemServico;
import com.produto.oficina.model.enums.StatusOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    // Query 1: Busca a OS e os ITENS DE SERVIÇO
    @Query("SELECT os FROM OrdemServico os LEFT JOIN FETCH os.itensServico WHERE os.id = :id")
    Optional<OrdemServico> findByIdWithItensServico(@Param("id") Long id);

    // Query 2: Busca a OS e as PEÇAS USADAS
    @Query("SELECT os FROM OrdemServico os LEFT JOIN FETCH os.pecasUsadas WHERE os.id = :id")
    Optional<OrdemServico> findByIdWithPecasUsadas(@Param("id") Long id);

    // Query 3: Busca a OS e as CONTAS A RECEBER
    @Query("SELECT os FROM OrdemServico os LEFT JOIN FETCH os.contaRecebers WHERE os.id = :id")
    Optional<OrdemServico> findByIdWithContasReceber(@Param("id") Long id);

    long countByStatus(StatusOS status);

    List<OrdemServico> findAllByDataFechamentoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    void deleteOrdemServicoById(Long id);
}
