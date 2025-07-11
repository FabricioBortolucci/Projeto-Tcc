package com.produto.oficina.service;

import com.produto.oficina.model.ContaReceber;
import com.produto.oficina.repository.ContaReceberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ContaReceberService {

    private final ContaReceberRepository contaReceberRepository;

    public ContaReceberService(ContaReceberRepository contaReceberRepository) {
        this.contaReceberRepository = contaReceberRepository;
    }

    public Page<ContaReceber> findAll(Pageable pageable) {
        return contaReceberRepository.findAll(pageable);
    }
}
