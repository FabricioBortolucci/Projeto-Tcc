package com.produto.oficina.service;

import com.produto.oficina.entity.Teste;
import com.produto.oficina.repository.TesteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TesteService {


    private final TesteRepository testeRepository;

    public TesteService(TesteRepository testeRepository) {
        this.testeRepository = testeRepository;
    }

    public void salvar(Teste teste) {
       testeRepository.save(teste);
    }

    public List<Teste> listar() {
       return testeRepository.findAll();
    }
}
