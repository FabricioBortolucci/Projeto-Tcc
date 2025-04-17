package com.produto.oficina.service;

import com.produto.oficina.model.Produto;
import com.produto.oficina.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {


    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Page<Produto> findAll(Pageable pageable) {
        return produtoRepository.findAll(pageable);
    }

    public void save(Produto produto) {
        produtoRepository.saveAndFlush(produto);
    }

    public void deleteProd(Long produto) {
        produtoRepository.deleteById(produto);
    }

    public Optional<Produto> findById(Long index) {
        return produtoRepository.findById(index);
    }
}
