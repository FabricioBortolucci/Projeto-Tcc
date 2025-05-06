package com.produto.oficina.service;

import com.produto.oficina.repository.MovimentacaoCaixaRepository;
import org.springframework.stereotype.Service;

@Service
public class MovimentacaoCaixaService {


    private final MovimentacaoCaixaRepository movimentacaoCaixaRepository;

    public MovimentacaoCaixaService(MovimentacaoCaixaRepository movimentacaoCaixaRepository) {
        this.movimentacaoCaixaRepository = movimentacaoCaixaRepository;
    }
}
