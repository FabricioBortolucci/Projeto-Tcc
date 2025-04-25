package com.produto.oficina.service;

import com.produto.oficina.dto.pessoaCad.TelefoneDto;
import com.produto.oficina.model.Telefone;
import com.produto.oficina.repository.TelefoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelefoneService {


    private final TelefoneRepository telefoneRepository;

    public TelefoneService(TelefoneRepository telefoneRepository) {
        this.telefoneRepository = telefoneRepository;
    }

    public Telefone buscaTelefonePrincipal() {
       return telefoneRepository.findTelefoneByTelPrincipalIsTrue();
    }

    public List<TelefoneDto> buscaTodosTelefonesByPessoaId(Long id) {
        return telefoneRepository.buscaTodosByPesId(id);
    }

    public Telefone buscaTelefoneById(Long id) {
        return telefoneRepository.findById(id).get();
    }
}
