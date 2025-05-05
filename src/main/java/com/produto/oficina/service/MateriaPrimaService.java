package com.produto.oficina.service;

import com.produto.oficina.model.MateriaPrima;
import com.produto.oficina.repository.MateriaPrimaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MateriaPrimaService {


    private final MateriaPrimaRepository materiaPrimaRepository;

    public MateriaPrimaService(MateriaPrimaRepository materiaPrimaRepository) {
        this.materiaPrimaRepository = materiaPrimaRepository;
    }

    public List<MateriaPrima> buscarMpsPorFornecedor(Long fornecedorId) {
        return materiaPrimaRepository.buscaPorFornecedor(fornecedorId);
    }
}
