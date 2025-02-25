package com.produto.oficina.service;

import com.produto.oficina.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {


    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }
}
