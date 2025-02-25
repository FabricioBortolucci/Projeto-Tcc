package com.produto.oficina.service;

import com.produto.oficina.repository.EstadoRepository;
import org.springframework.stereotype.Service;

@Service
public class EstadoService {


    private final EstadoRepository estadoRepository;

    public EstadoService(EstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }
}
