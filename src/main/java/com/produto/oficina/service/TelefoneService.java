package com.produto.oficina.service;

import com.produto.oficina.repository.TelefoneRepository;
import org.springframework.stereotype.Service;

@Service
public class TelefoneService {


    private final TelefoneRepository telefoneRepository;

    public TelefoneService(TelefoneRepository telefoneRepository) {
        this.telefoneRepository = telefoneRepository;
    }
}
