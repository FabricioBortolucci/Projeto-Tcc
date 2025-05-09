package com.produto.oficina.service;

import com.produto.oficina.dto.AtividadeRecenteDTO;
import com.produto.oficina.dto.DashboardPageDTO;
import com.produto.oficina.dto.DashboardStatsDTO;
import com.produto.oficina.model.Produto;
import com.produto.oficina.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashBoardService {


    private final ProdutoRepository produtoRepository;

    // Injete outros repositórios aqui conforme necessário
    // private final OrdemServicoRepository ordemServicoRepository;
    // private final PessoaRepository pessoaRepository;

    public DashboardPageDTO getDashboardPageData() {
        DashboardStatsDTO stats = getDashboardStats();
        List<AtividadeRecenteDTO> alertasEstoque = getAlertasDeEstoque();
        return new DashboardPageDTO(stats, alertasEstoque);
    }

    private DashboardStatsDTO getDashboardStats() {
        long totalProdutos = produtoRepository.count();
        // Usa o novo método do repositório para a contagem
        long produtosBaixoEstoqueCount = produtoRepository.countProdutosEmEstoqueBaixo();

        // Dados Mockados (substitua com chamadas reais)
        long ordensAbertasMock = 12L;
        long clientesAtivosMock = 152L;
        String faturamentoMesMock = "R$ 12.345,67"; // Mantenha ou substitua

        return new DashboardStatsDTO(
                ordensAbertasMock,
                clientesAtivosMock,
                faturamentoMesMock,
                totalProdutos,
                produtosBaixoEstoqueCount
        );
    }

    private List<AtividadeRecenteDTO> getAlertasDeEstoque() {
        Pageable topAlertas = PageRequest.of(0, 5); // Mostrar os top 5 alertas
        // Usa o novo método do repositório para buscar a lista
        List<Produto> produtosComEstoqueBaixo = produtoRepository.findProdutosComEstoqueBaixo(topAlertas);

        if (produtosComEstoqueBaixo.isEmpty()) {
            return List.of(new AtividadeRecenteDTO(
                    "Nenhum alerta de estoque",
                    "Todos os produtos com estoque > 3.",
                    "Verificado",
                    null,
                    "bi-check-circle-fill"
            ));
        }

        return produtosComEstoqueBaixo.stream()
                .map(produto -> new AtividadeRecenteDTO(
                        "Estoque Baixo: " + produto.getNome(), // Supondo que Produto tem getNome()
                        "Estoque Atual: " + produto.getEstoque() + " (Limite: <=3)", // Supondo que Produto tem getEstoque()
                        "Alerta Crítico",
                        "/produto/visualizar/" + produto.getId(), // Supondo que Produto tem getId()
                        "bi-exclamation-triangle-fill text-danger"
                ))
                .collect(Collectors.toList());
    }
}
