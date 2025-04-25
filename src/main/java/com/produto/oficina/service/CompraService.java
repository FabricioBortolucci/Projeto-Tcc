package com.produto.oficina.service;

import com.produto.oficina.model.Compra;
import com.produto.oficina.model.Produto;
import com.produto.oficina.repository.CompraRepository;
import com.produto.oficina.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompraService {


    private final CompraRepository compraRepository;

    public CompraService(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public Page<Compra> findAll(Pageable pageable) {
        return compraRepository.findAll(pageable);
    }

    public void save(Compra compra) {

        compraRepository.saveAndFlush(compra);
    }

    public void deleteProd(Long compraId) {
        compraRepository.deleteById(compraId);
    }

    public Optional<Compra> findById(Long index) {
        return compraRepository.findById(index);
    }
}
