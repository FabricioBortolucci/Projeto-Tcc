package com.produto.oficina.service;

import com.produto.oficina.dto.FuncionarioDTO;
import com.produto.oficina.dto.pessoaCad.EnderecoDto;
import com.produto.oficina.dto.pessoaCad.PessoaDto;
import com.produto.oficina.dto.pessoaCad.TelefoneDto;
import com.produto.oficina.model.*;
import com.produto.oficina.model.enums.PesTipo;
import com.produto.oficina.repository.PessoaRepository;
import com.produto.oficina.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PessoaService {

    private final PessoaRepository repository;

    private final EnderecoService enderecoService;

    private final TelefoneService telefoneService;

    private final CidadeService cidadeService;

    private final UsuarioRepository usuarioRepository;


    public PessoaService(PessoaRepository pessoaRepository, EnderecoService enderecoService, TelefoneService telefoneService, CidadeService cidadeService, UsuarioRepository usuarioRepository) {
        this.repository = pessoaRepository;
        this.enderecoService = enderecoService;
        this.telefoneService = telefoneService;
        this.cidadeService = cidadeService;
        this.usuarioRepository = usuarioRepository;
    }

    public List<FuncionarioDTO> buscaFuncAtivoPorUsuNull() {
        return repository.findFuncs();
    }

    public List<FuncionarioDTO> buscaFuncsAtual(Long idUsuario) {
        return repository.findFuncsEAtual(idUsuario);
    }

    public Page<PessoaDto> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(p -> new PessoaDto(
                        p.getId(),
                        p.getPesNome(),
                        p.getPesCpfCnpj(),
                        p.getPesTipo().toString(),
                        p.getPesAtivo(),
                        p.getPesEmail()
                ));
    }

    @Transactional
    public void salvarPessoa(PessoaDto pessoaDto, List<EnderecoDto> enderecosDtoList, List<TelefoneDto> telefonesDtoList) {
        if (pessoaDto != null && enderecosDtoList != null && telefonesDtoList != null) {
            Pessoa pessoa = mapearPessoa(pessoaDto);
            mapearEndereco(enderecosDtoList, pessoa);
            mapearTelefone(telefonesDtoList, pessoa);
            repository.save(pessoa);
        }
    }

    private void mapearTelefone(List<TelefoneDto> telefonesDtoList, Pessoa pessoa) {
        for (TelefoneDto telefone : telefonesDtoList) {
            Telefone te;
            if (telefone.getId() != null) {
                te = telefoneService.buscaTelefoneById(telefone.getId());
            } else {
                te = new Telefone();
            }
            te.setTelNumero(telefone.getTelNumero());
            te.setTipo(telefone.getTipo());
            te.setTelPrincipal(telefone.isTelPrincipal());
            te.setPessoa(pessoa);
            pessoa.getTelefones().add(te);

        }
    }

    private Pessoa mapearPessoa(PessoaDto pessoaDto) {
        Pessoa pessoa;
        if (pessoaDto.getId() != null) {
            pessoa = repository.findById(pessoaDto.getId()).get();
            pessoa.setPesDataAtualizacao(LocalDateTime.now());
        } else {
            pessoa = new Pessoa();
            pessoa.setPesDataCadastro(LocalDateTime.now());
        }
        if (Objects.equals(pessoaDto.getPesFisicoJuridico(), "Fisíca")) {
            pessoa.setPesDataNascimento(pessoaDto.getPesDataNascimento());
            pessoa.setPesGenero(pessoaDto.getPesGenero());
        }
        pessoa.setPesNome(pessoaDto.getPesNome());
        pessoa.setPesEmail(pessoaDto.getPesEmail());
        pessoa.setPesCpfCnpj(pessoaDto.getPesCpfCnpj());
        pessoa.setPesTipo(pessoaDto.getPesTipo());
        pessoa.setPesAtivo(true);
        pessoa.setPesFisicoJuridico(pessoaDto.getPesFisicoJuridico());

        return pessoa;
    }

    public void mapearEndereco(List<EnderecoDto> enderecosDtoList, Pessoa pessoa) {
        for (EnderecoDto endereco : enderecosDtoList) {
            Endereco end;
            if (endereco.getId() != null) {
                end = enderecoService.buscaEndById(endereco.getId());
            } else {
                end = new Endereco();
            }
            end.setEndBairro(endereco.getEndBairro());
            end.setEndCep(endereco.getEndCep());
            end.setEndRua(endereco.getEndRua());
            end.setEndNumero(endereco.getEndNumero());
            end.setEndPrincipal(endereco.isEndPrincipal());
            end.setCidade(cidadeService.buscaCidadePorId(endereco.getCidadeId()));
            end.setPessoa(pessoa);
            pessoa.getEnderecos().add(end);
        }
    }


    public Optional<Pessoa> buscaFornecedorPorId(Long fornecedorId) {
        return repository.findById(fornecedorId);
    }

    public PessoaDto buscaPessoaEnderecosTelefones(Long index) {
        PessoaDto pes = repository.findPessoaById(index);
        if (pes != null) {
            pes.setTelefones(telefoneService.buscaTodosTelefonesByPessoaId(pes.getId()));
            pes.setEnderecos(enderecoService.buscaTodosEnderecosByPessoaId(pes.getId()));
            for (EnderecoDto end : pes.getEnderecos()) {
                end.setCidade(cidadeService.findCidadeById(end.getCidadeId()));
            }
        }
        return pes;
    }

    public void desativarPessoa(Long index) {
        repository.findById(index).ifPresent(pessoa -> {
            pessoa.setPesAtivo(false);
            repository.saveAndFlush(pessoa);
        });
    }

    public List<Pessoa> buscaFornecedores() {
        return repository.findAllByPesAtivoAndPesTipo(true, PesTipo.FORNECEDOR);
    }

    public static String getUsuarioLogadoUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        }
        return null;
    }

    public Usuario buscaUsuarioLogado() {
        String username = getUsuarioLogadoUsername();
        if (username != null) {
            return usuarioRepository.findUsuarioByUsuNome(username);
        }
        return null;
    }

    public Pessoa buscaPessoaLogada() {
        String username = getUsuarioLogadoUsername();
        if (username != null) {
            Usuario usuario = usuarioRepository.findUsuarioByUsuNome(username);
            Pessoa pes = repository.findPessoaByUsuario(usuario);
            if (usuario != null && usuario.getPessoaRel() != null) {
                return pes;
            }
        }
        return null;
    }

    public void salvarEdit(Pessoa pessoa) {
        repository.save(pessoa);
    }

    public List<Pessoa> buscaClientes() {
        return repository.findAllByPesAtivoAndPesTipo(true, PesTipo.CLIENTE);
    }

    public List<Pessoa> buscaFuncionarios() {
        return repository.findAllByPesAtivoAndPesTipo(true, PesTipo.FUNCIONARIO);
    }
}
