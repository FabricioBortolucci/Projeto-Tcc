package com.produto.oficina.service;

import com.produto.oficina.repository.CidadeRepository;
import org.springframework.stereotype.Service;

@Service
public class CidadeService {


    private final CidadeRepository cidadeRepository;

    public CidadeService(CidadeRepository cidadeRepository) {
        this.cidadeRepository = cidadeRepository;
    }
}
