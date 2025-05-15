package com.produto.oficina.service;

import com.produto.oficina.dto.CompraDTO;
import com.produto.oficina.model.Compra;
import com.produto.oficina.model.ItemCompra;
import com.produto.oficina.model.Produto;
import com.produto.oficina.repository.CompraRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CompraService {


    private final CompraRepository compraRepository;
    private final ProdutoService produtoService;

    public CompraService(CompraRepository compraRepository, ProdutoService produtoService) {
        this.compraRepository = compraRepository;
        this.produtoService = produtoService;
    }

    public Page<Compra> findAll(Pageable pageable) {
        return compraRepository.findAll(pageable);
    }

    public void save(CompraDTO compra) {

    }

    public void deleteProd(Long compraId) {
        compraRepository.deleteById(compraId);
    }

    public Optional<Compra> findById(Long index) {
        return compraRepository.findById(index);
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
