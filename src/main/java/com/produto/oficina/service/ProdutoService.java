package com.produto.oficina.service;

import com.produto.oficina.model.Produto;
import com.produto.oficina.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {


    private final ProdutoRepository produtoRepository;


    public Page<Produto> findAll(Pageable pageable) {
        return produtoRepository.findAll(pageable);
    }

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public void save(Produto produto) {
        produtoRepository.saveAndFlush(produto);
    }

    public void deleteProd(Long produto) {
        produtoRepository.findById(produto).ifPresent(produto1 -> {
            produto1.setAtivo(false);
            produtoRepository.saveAndFlush(produto1);
        });
    }

    public Produto findById(Long index) {
        if (produtoRepository.findById(index).isPresent()) {
            return produtoRepository.findById(index).get();
        }
        return null;
    }

    public Produto findByIdView(Long index) {
        return produtoRepository.findById(index).get();
    }

    public List<Produto> buscarPorNome(String query) {
        return produtoRepository.findByNomeContainingIgnoreCase(query);
    }

}
