package com.produto.oficina.service;

import com.produto.oficina.repository.LancamentoFinanceiroRepository;
import org.springframework.stereotype.Service;

@Service
public class LancamentoFinanceiroService {

    private final LancamentoFinanceiroRepository lancamentoFinanceiroRepository;

    public LancamentoFinanceiroService(LancamentoFinanceiroRepository lancamentoFinanceiroRepository) {
        this.lancamentoFinanceiroRepository = lancamentoFinanceiroRepository;
    }
}
