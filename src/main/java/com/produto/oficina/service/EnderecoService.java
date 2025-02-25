package com.produto.oficina.service;

import com.produto.oficina.repository.CidadeRepository;
import com.produto.oficina.repository.EnderecoRepository;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {


    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }
}
