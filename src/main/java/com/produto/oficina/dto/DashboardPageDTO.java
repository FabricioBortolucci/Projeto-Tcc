package com.produto.oficina.dto;

import java.util.List;

public record DashboardPageDTO(
         DashboardStatsDTO estatisticas,
        List<AtividadeRecenteDTO> atividadesRecentes
) {}