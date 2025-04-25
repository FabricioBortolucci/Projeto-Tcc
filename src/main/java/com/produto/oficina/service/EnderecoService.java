package com.produto.oficina.service;

import com.produto.oficina.dto.pessoaCad.EnderecoDto;
import com.produto.oficina.model.Endereco;
import com.produto.oficina.repository.CidadeRepository;
import com.produto.oficina.repository.EnderecoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnderecoService {


    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public List<EnderecoDto> buscaTodosEnderecosByPessoaId(Long id) {
       return enderecoRepository.buscaTodosByPesId(id);
    }

    public Endereco buscaEndById(Long id) {
        return enderecoRepository.findById(id).get();
    }
}
