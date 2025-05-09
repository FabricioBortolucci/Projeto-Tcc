package com.produto.oficina.dto;

public record AtividadeRecenteDTO(
        String titulo,      // Ex: "Estoque Baixo: Nome do Produto"
        String descricao,   // Ex: "Estoque Atual: X (Limite: <=3)"
        String tempo,       // Ex: "Alerta Crítico"
        String link,        // Link para o produto
        String icon         // Ícone de alerta (ex: "bi-exclamation-triangle-fill text-danger")
) {}