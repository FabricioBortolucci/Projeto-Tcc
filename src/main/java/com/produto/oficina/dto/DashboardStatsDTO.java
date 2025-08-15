package com.produto.oficina.dto;

import java.math.BigDecimal;

public record DashboardStatsDTO(
        long ordensServicoAbertas,
        long clientesAtivos,
        BigDecimal faturamentoMes,
        long totalProdutosCadastrados,
        long produtosEmEstoqueBaixo
) {}