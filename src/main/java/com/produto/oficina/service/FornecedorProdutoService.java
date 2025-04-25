package com.produto.oficina.service;

import com.produto.oficina.model.FornecedorProduto;
import com.produto.oficina.model.Produto;
import com.produto.oficina.repository.FornecedorProdutoRepository;
import com.produto.oficina.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FornecedorProdutoService {


    private final FornecedorProdutoRepository fornecedorProdutoRepository;

    public List<Produto> buscarProdutosPorFornecedor(Long fornecedorId) {
        return fornecedorProdutoRepository.findByFornecedorIdAndAtivoTrue(fornecedorId)
                .stream()
                .map(FornecedorProduto::getProduto)
                .collect(Collectors.toList());
    }

}
