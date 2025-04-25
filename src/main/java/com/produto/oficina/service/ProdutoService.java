package com.produto.oficina.service;

import com.produto.oficina.dto.FornecedorDTO;
import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.Produto;
import com.produto.oficina.repository.PessoaRepository;
import com.produto.oficina.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {


    private final ProdutoRepository produtoRepository;
    private final PessoaRepository pessoaRepository;


    public Page<Produto> findAll(Pageable pageable) {
        return produtoRepository.findAll(pageable);
    }

    public void save(Produto produto) {
        produto.setAtivo(true);
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

    public List<FornecedorDTO> buscaFornecedores() {
        return pessoaRepository.findFornecedores();
    }

}
