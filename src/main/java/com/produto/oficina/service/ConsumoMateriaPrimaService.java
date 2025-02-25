package com.produto.oficina.service;

import com.produto.oficina.repository.ConsumoMateriaPrimaRepository;
import org.springframework.stereotype.Service;

@Service
public class ConsumoMateriaPrimaService {


    private final ConsumoMateriaPrimaRepository consumoMateriaPrimaRepository;

    public ConsumoMateriaPrimaService(ConsumoMateriaPrimaRepository consumoMateriaPrimaRepository) {
        this.consumoMateriaPrimaRepository  = consumoMateriaPrimaRepository;
    }
}
