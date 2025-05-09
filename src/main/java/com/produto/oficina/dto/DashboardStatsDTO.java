package com.produto.oficina.dto;

public record DashboardStatsDTO(
        long ordensServicoAbertas,
        long clientesAtivos,
        String faturamentoMes,
        long totalProdutosCadastrados,
        long produtosEmEstoqueBaixo
) {}