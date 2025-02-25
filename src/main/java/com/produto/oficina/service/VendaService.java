package com.produto.oficina.service;

import com.produto.oficina.repository.VendaRepository;
import org.springframework.stereotype.Service;

@Service
public class VendaService {


    private final VendaRepository vendaRepository;

    public VendaService(VendaRepository vendaRepository) {
        this.vendaRepository = vendaRepository;
    }
}
