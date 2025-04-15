package com.produto.oficina.service;

import com.produto.oficina.dto.FuncionarioDTO;
import com.produto.oficina.dto.pessoaCad.EnderecoDto;
import com.produto.oficina.dto.pessoaCad.PessoaDto;
import com.produto.oficina.dto.pessoaCad.TelefoneDto;
import com.produto.oficina.model.*;
import com.produto.oficina.repository.PessoaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
                        p.getPesTipo(),
                        p.getPesAtivo(),
                        p.getPesEmail()
                )).toList();
    }

    public void cadastrarPessoa(PessoaDto objDto) {
        if (objDto.getId() != null) {
            Pessoa pes = new Pessoa();
            pes.setId(objDto.getId());

        }
    }

    public void salvarPessoa(PessoaDto pessoaDto, List<EnderecoDto> enderecosDtoList, List<TelefoneDto> telefonesDtoList) {
        if (pessoaDto != null && enderecosDtoList != null && telefonesDtoList != null) {
            Pessoa pessoa = new Pessoa();
            pessoa.setPesNome(pessoaDto.getPesNome());
            pessoa.setPesEmail(pessoaDto.getPesEmail());
            pessoa.setPesCpfCnpj(pessoaDto.getPesCpfCnpj());
            pessoa.setPesTipo(pessoaDto.getPesTipo());
            pessoa.setPesAtivo(true);
            pessoa.setPesDataNascimento(pessoaDto.getPesDataNascimento());
            pessoa.setPesGenero(pessoaDto.getPesGenero());
            pessoa.setPesFisicoJuridico(pessoaDto.getPesFisicoJuridico());
            pessoa.setPesRg(pessoaDto.getPesRg());
            pessoa.setPesInscricaoEstadual(pessoaDto.getPesInscricaoEstadual());
            pessoa.setPesDataCadastro(LocalDateTime.now());
            
            pessoa.setEnderecos(new ArrayList<>());
            for (EnderecoDto endereco : enderecosDtoList) {
                Endereco end = new Endereco();
                end.setEndBairro(endereco.getEndBairro());
                end.setEndCep(endereco.getEndCep());
                end.setEndRua(endereco.getEndRua());
                end.setEndNumero(endereco.getEndNumero());
                end.setEndPrincipal(endereco.isEndPrincipal());

                Cidade cidade = getCidade(endereco);
                end.setCidade(cidade);
                end.setPessoa(pessoa);
                pessoa.getEnderecos().add(end);
            }

            pessoa.setTelefones(new ArrayList<>());
            for (TelefoneDto telefone : telefonesDtoList) {
                Telefone te = new Telefone();
                te.setTelNumero(telefone.getTelNumero());
                te.setTipo(telefone.getTipo());
                te.setTelPrincipal(telefone.isTelPrincipal());

                pessoa.getTelefones().add(te);
            }

            repository.save(pessoa);
        }
    }

    private static Cidade getCidade(EnderecoDto endereco) {
        Cidade cidade = new Cidade();
        cidade.setCidNome(endereco.getCidade().getCidNome());

        Estado estado = new Estado();
        estado.setEstNome(endereco.getCidade().getEstado().getEstNome());
        estado.setSigla(endereco.getCidade().getEstado().getSigla());

        cidade.setEstado(estado);
        return cidade;
    }
}
