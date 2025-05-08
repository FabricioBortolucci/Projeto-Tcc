package com.produto.oficina.service;

import com.produto.oficina.model.Servico;
import com.produto.oficina.repository.ServicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    public Page<Servico> buscaTodosPaginado(Pageable pageable) {
        return servicoRepository.findAll(pageable);
    }

    public void salvar(Servico servico) {
        servicoRepository.saveAndFlush(servico);
    }

    public Servico buscaServico(Long index) {
        return servicoRepository.findById(index).get();
    }

    public void desativarServico(Long index) {
        Servico servico = servicoRepository.findById(index).get();
        servico.setAtivo(false);
        servicoRepository.saveAndFlush(servico);
    }
}
