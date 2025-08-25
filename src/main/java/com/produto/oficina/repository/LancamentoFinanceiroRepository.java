package com.produto.oficina.repository;

import com.produto.oficina.model.LancamentoFinanceiro;
import com.produto.oficina.model.OrdemServico;
import com.produto.oficina.model.enums.NaturezaContaPlanoContas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LancamentoFinanceiroRepository extends JpaRepository<LancamentoFinanceiro, Long> {

    List<LancamentoFinanceiro> findByOrdemServico(OrdemServico ordemServico);

    @Query("SELECT COALESCE(SUM(l.valor), 0) FROM LancamentoFinanceiro l WHERE l.dataCompetencia BETWEEN :dataInicio AND :dataFim AND l.planoDeContas.naturezaConta = :natureza")
    BigDecimal sumValorByPeriodoAndNatureza(@Param("dataInicio") LocalDate dataInicio,
                                            @Param("dataFim") LocalDate dataFim,
                                            @Param("natureza") NaturezaContaPlanoContas natureza);
}
