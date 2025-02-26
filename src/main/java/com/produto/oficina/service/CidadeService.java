package com.produto.oficina.service;

import com.produto.oficina.model.Cidade;
import com.produto.oficina.repository.CidadeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CidadeService {


    private final CidadeRepository cidadeRepository;

    public CidadeService(CidadeRepository cidadeRepository) {
        this.cidadeRepository = cidadeRepository;
    }

    public List<Cidade> listar() {
        return cidadeRepository.findAll();
    }
}
