package com.produto.oficina.service;

import com.produto.oficina.dto.AtividadeRecenteDTO;
import com.produto.oficina.dto.DashboardPageDTO;
import com.produto.oficina.dto.DashboardStatsDTO;
import com.produto.oficina.model.Produto;
import com.produto.oficina.model.enums.PesTipo;
import com.produto.oficina.repository.OrdemServicoRepository;
import com.produto.oficina.repository.PessoaRepository;
import com.produto.oficina.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashBoardService {


    private final ProdutoRepository produtoRepository;
    private final PessoaRepository pessoaRepository;
    private final OrdemServicoService ordemServicoService;


    public DashboardPageDTO getDashboardPageData() {
        DashboardStatsDTO stats = getDashboardStats();
        List<AtividadeRecenteDTO> alertasEstoque = getAlertasDeEstoque();
        return new DashboardPageDTO(stats, alertasEstoque);
    }

    private DashboardStatsDTO getDashboardStats() {
        long totalProdutos = produtoRepository.count();
        long produtosBaixoEstoqueCount = produtoRepository.countProdutosEmEstoqueBaixo();

        long ordensAbertasMock = ordemServicoService.countByStatusAbertaEexecucao();
        long clientesAtivosMock = pessoaRepository.countPessoaByPesAtivoAndPesTipo(true, PesTipo.CLIENTE);
        BigDecimal faturamentoMesMock = ordemServicoService.buscaFaturamentoMensal();

        return new DashboardStatsDTO(
                ordensAbertasMock,
                clientesAtivosMock,
                faturamentoMesMock,
                totalProdutos,
                produtosBaixoEstoqueCount
        );
    }

    private List<AtividadeRecenteDTO> getAlertasDeEstoque() {
        Pageable topAlertas = PageRequest.of(0, 5);
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
                        "Estoque Baixo: " + produto.getNome(),
                        "Estoque Atual: " + produto.getEstoque() + " (Limite: <=3)",
                        "Alerta Crítico",
                        "/produto/visualizar/" + produto.getId(),
                        "bi-exclamation-triangle-fill text-danger"
                ))
                .collect(Collectors.toList());
    }
}
