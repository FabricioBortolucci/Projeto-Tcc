package com.produto.oficina.service;

import com.produto.oficina.repository.PessoaRepository;
import org.springframework.stereotype.Service;

@Service
public class PessoaService {


    private final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }
}
