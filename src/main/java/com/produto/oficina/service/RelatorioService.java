package com.produto.oficina.service;

import com.produto.oficina.dto.reports.DRECompletoDTO;
import com.produto.oficina.model.enums.NaturezaContaPlanoContas;
import com.produto.oficina.repository.LancamentoFinanceiroRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class RelatorioService {

    private final LancamentoFinanceiroRepository lancamentoFinanceiroRepository;

    public RelatorioService(LancamentoFinanceiroRepository lancamentoFinanceiroRepository) {
        this.lancamentoFinanceiroRepository = lancamentoFinanceiroRepository;
    }

    public DRECompletoDTO gerarRelatorioDRE(LocalDate dataInicio, LocalDate dataFim) {
        BigDecimal totalReceitas = lancamentoFinanceiroRepository.sumValorByPeriodoAndNatureza(dataInicio, dataFim, NaturezaContaPlanoContas.RECEITA);
        BigDecimal totalCustos = lancamentoFinanceiroRepository.sumValorByPeriodoAndNatureza(dataInicio, dataFim, NaturezaContaPlanoContas.CUSTO);
        BigDecimal totalDespesas = lancamentoFinanceiroRepository.sumValorByPeriodoAndNatureza(dataInicio, dataFim, NaturezaContaPlanoContas.DESPESA);

        BigDecimal lucroBruto = totalReceitas.subtract(totalCustos);
        BigDecimal lucroLiquido = lucroBruto.subtract(totalDespesas);

        return new DRECompletoDTO(
                totalReceitas,
                totalCustos,
                totalDespesas,
                lucroBruto,
                lucroLiquido
        );
    }
}
