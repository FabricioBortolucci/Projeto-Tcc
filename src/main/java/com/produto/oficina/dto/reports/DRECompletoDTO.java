package com.produto.oficina.dto.reports;

import java.math.BigDecimal;

public record DRECompletoDTO(
        BigDecimal totalReceitas,
        BigDecimal totalCustos,
        BigDecimal totalDespesas,
        BigDecimal lucroBruto,
        BigDecimal lucroLiquido
) {}
