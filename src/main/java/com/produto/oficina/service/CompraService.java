package com.produto.oficina.service;

import com.produto.oficina.dto.CompraDTO;
import com.produto.oficina.model.*;
import com.produto.oficina.model.enums.PlanoPagamento;
import com.produto.oficina.model.enums.StatusCompra;
import com.produto.oficina.model.enums.StatusConta;
import com.produto.oficina.model.enums.TipoMovimentacao;
import com.produto.oficina.repository.CompraRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompraService {


    private final CompraRepository compraRepository;
    private final ProdutoService produtoService;
    private final CaixaService caixaService;
    private final ContaPagarService contaPagarService;

    public CompraService(CompraRepository compraRepository, ProdutoService produtoService, CaixaService caixaService, ContaPagarService contaPagarService) {
        this.compraRepository = compraRepository;
        this.produtoService = produtoService;
        this.caixaService = caixaService;
        this.contaPagarService = contaPagarService;
    }

    public Page<Compra> findAll(Pageable pageable) {
        return compraRepository.findAll(pageable);
    }

    @Transactional
    public void save(CompraDTO compra) {
        if (compra != null) {
            Compra novaCompra = dtoToClass(compra);

            List<ContaPagar> contasPagar = new ArrayList<>();
            ContaPagar contaPagar;
            if (novaCompra.getPlanoPagamento().equals(PlanoPagamento.APRAZO)) {
                LocalDate dataCompra = novaCompra.getDataCompra().toLocalDate();
                for (int i = 1; i <= compra.getTotalParcelas(); i++) {
                    contaPagar = new ContaPagar();
                    contaPagar.setCompra(novaCompra);
                    contaPagar.setFornecedor(novaCompra.getFornecedor());
                    contaPagar.setNumeroParcela(i);
                    contaPagar.setStatus(StatusConta.PENDENTE);
                    contaPagar.setTotalParcelas(novaCompra.getTotalParcelas());
                    contaPagar.setValorTotalOriginal(novaCompra.getValorTotal());
                    contaPagar.setValor(novaCompra.getValorTotal().divide(BigDecimal.valueOf(novaCompra.getTotalParcelas()), RoundingMode.HALF_UP));
                    LocalDate dataVencimento = dataCompra.plusMonths(i - 1);
                    contaPagar.setDataVencimento(dataVencimento);
                    contasPagar.add(contaPagar);
                }
                novaCompra.setContaPagars(contasPagar);
            } else {
                contaPagar = new ContaPagar();
                contaPagar.setCompra(novaCompra);
                contaPagar.setFornecedor(novaCompra.getFornecedor());
                contaPagar.setNumeroParcela(1);
                contaPagar.setStatus(StatusConta.PAGO);
                contaPagar.setTipoPagamento(novaCompra.getTipoPagamento());
                contaPagar.setTotalParcelas(novaCompra.getTotalParcelas());
                contaPagar.setValorTotalOriginal(novaCompra.getValorTotal());
                contaPagar.setValor(novaCompra.getValorTotal());
                contaPagar.setDataVencimento(novaCompra.getDataCompra().toLocalDate());
                contaPagar.setDataPagamento(LocalDate.now());
                contasPagar.add(contaPagar);
                novaCompra.setContaPagars(contasPagar);
            }

            for (ItemCompra item : novaCompra.getItens()) {
                Produto prodAtual = produtoService.findById(item.getProduto().getId());
                prodAtual.setEstoque(prodAtual.getEstoque() + item.getQuantidade());
                produtoService.save(prodAtual);
            }

            novaCompra.setStatusCompra(StatusCompra.FINALIZADA);
            novaCompra.setDataCompraFinalizada(LocalDateTime.now());
            compraRepository.save(novaCompra);

            if (novaCompra.getPlanoPagamento().equals(PlanoPagamento.AVISTA)) {
                MovimentacaoCaixa novoMovimento = new MovimentacaoCaixa();
                Caixa caixaAtual = caixaService.buscaCaixaAtualAberto();

                novoMovimento.setCaixa(caixaAtual);
                novoMovimento.setDataMovimentacao(LocalDateTime.now());
                novoMovimento.setTipo(TipoMovimentacao.SAIDA);
                novoMovimento.setContaPagar(novaCompra.getContaPagars().getFirst());
                novoMovimento.setValor(novaCompra.getValorTotal());
                novoMovimento.setDescricao("Compra de produtos realizada em " + novaCompra.getDataCompra().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ".");
                caixaAtual.getMovimentacoes().add(novoMovimento);
                caixaService.salvarCaixaAposMovimento(caixaAtual);
            }
        }
    }

    private Compra dtoToClass(CompraDTO compra) {
        Compra novaCompra = new Compra();
        novaCompra.setDataCompra(compra.getDataCompra());
        novaCompra.setFornecedor(compra.getFornecedor());
        novaCompra.setPlanoPagamento(compra.getPlanoPagamento());
        novaCompra.setTipoPagamento(compra.getTipoPagamento());
        novaCompra.setValorTotal(compra.getCalculaValorTotalItens());
        novaCompra.setObservacao(compra.getObservacao());
        novaCompra.setItens(compra.getItemCompraList());
        novaCompra.setDataCompraFinalizada(LocalDateTime.now());
        novaCompra.setTotalParcelas(compra.getTotalParcelas());
        for (ItemCompra item : novaCompra.getItens()) {
            item.setCompra(novaCompra);
        }
        return novaCompra;
    }

    public void salvarCompraAberta(CompraDTO compraDTO) {
        Compra novaCompra = dtoToClass(compraDTO);
        novaCompra.setStatusCompra(StatusCompra.ABERTA);
        compraRepository.save(novaCompra);
    }


    public Compra findById(Long index) {
        return compraRepository.findCompraByIdOrderByIdAsc(index);
    }

    public void adicionarItemCompra(CompraDTO compraDTO, Long produtoId, Integer quantidade, BigDecimal valorCusto) {
        Produto produto = produtoService.findById(produtoId);

        if (produto == null) {
            return;
        }

        for (ItemCompra item : compraDTO.getItemCompraList()) {
            if (item.getProduto().getId().equals(produtoId)) {
                if (item.getValorUnitario().equals(valorCusto)) {
                    item.setQuantidade(item.getQuantidade() + quantidade);
                    item.setValorTotal(item.getSubTotal());
                    return;
                }
            }
        }

        ItemCompra novoItem = new ItemCompra();
        novoItem.setProduto(produto);
        novoItem.setValorUnitario(valorCusto);
        novoItem.setQuantidade(quantidade);
        novoItem.setValorTotal(novoItem.getSubTotal());
        compraDTO.getItemCompraList().add(novoItem);
    }

}
