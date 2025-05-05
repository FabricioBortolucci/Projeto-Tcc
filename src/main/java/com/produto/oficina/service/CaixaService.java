package com.produto.oficina.service;

import com.produto.oficina.model.Caixa;
import com.produto.oficina.model.Compra;
import com.produto.oficina.repository.CaixaRepository;
import com.produto.oficina.repository.CompraRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CaixaService {


    private final CaixaRepository caixaRepository;

    public CaixaService(CaixaRepository caixaRepository) {
        this.caixaRepository = caixaRepository;
    }


    public Page<Caixa> findAll(Pageable pageable) {
        return caixaRepository.findAll(pageable);
    }

    public void save(Caixa caixa) {
        caixaRepository.saveAndFlush(caixa);
    }

    public Optional<Caixa> findById(Long index) {
        return caixaRepository.findById(index);
    }

    public boolean verificaCaixaAberto() {
        Caixa caixaHoje = caixaRepository.findCaixaByDataCadastro(LocalDate.now());
        return caixaHoje == null;
    }
}
