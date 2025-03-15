package com.produto.oficina.service;

import com.produto.oficina.dto.FuncionarioDTO;
import com.produto.oficina.dto.pessoaCad.PessoaDto;
import com.produto.oficina.model.Pessoa;
import com.produto.oficina.repository.PessoaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PessoaService {


    private final PessoaRepository repository;

    private final EnderecoService enderecoService;

    private final TelefoneService telefoneService;


    public PessoaService(PessoaRepository pessoaRepository, EnderecoService enderecoService, TelefoneService telefoneService) {
        this.repository = pessoaRepository;
        this.enderecoService = enderecoService;
        this.telefoneService = telefoneService;
    }

    public List<FuncionarioDTO> buscaFuncAtivoPorUsuNull() {
        return repository.findFuncs();
    }

    public List<PessoaDto> listarTodos() {
        return repository.findAll()
                .stream()
                .map(p -> new PessoaDto(
                        p.getId(),
                        p.getPesNome(),
                        p.getPesCpfCnpj(),
                        p.getPesCliente(),
                        p.getPesAtivo()
                )).toList();
    }

    public void cadastrarPessoa(PessoaDto objDto) {
        if (objDto.getId() != null) {
            Pessoa pes = new Pessoa();
            pes.setId(objDto.getId());

        }
    }

}
