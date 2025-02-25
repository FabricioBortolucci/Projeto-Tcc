package com.produto.oficina.service;

import com.produto.oficina.repository.MateriaPrimaRepository;
import org.springframework.stereotype.Service;

@Service
public class MateriaPrimaService {


    private final MateriaPrimaRepository materiaPrimaRepository;

    public MateriaPrimaService(MateriaPrimaRepository materiaPrimaRepository) {
        this.materiaPrimaRepository = materiaPrimaRepository;
    }
}
