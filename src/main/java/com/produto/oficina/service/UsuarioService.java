package com.produto.oficina.service;

import com.produto.oficina.dto.UsuarioDTO;
import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.Usuario;
import com.produto.oficina.model.enums.Role;
import com.produto.oficina.repository.PessoaRepository;
import com.produto.oficina.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PessoaRepository pessoaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PessoaRepository pessoaRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.pessoaRepository = pessoaRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public void cadastrarUsuario(UsuarioDTO userDTO) {
        if (userDTO != null) {
            Pessoa funcionarioRel = pessoaRepository.findById(userDTO.getIdFunc()).get();
            Usuario usuario;

            if (userDTO.getId() != null) {
                // Já existe
                usuario = usuarioRepository.findById(userDTO.getId()).get();
                usuario.setUsuNome(userDTO.getUsuNome());
                usuario.setRole(userDTO.getRole());
                usuario.setUsuSenha(passwordEncoder.encode(userDTO.getUsuSenha()));
                usuario.setAtivo(userDTO.isAtivo());
                if (usuario.getPessoaRel() != funcionarioRel && usuario.isAtivo()) {
                    Pessoa funcAntigo = pessoaRepository.findById(
                            usuario.getPessoaRel() != null ?
                                    usuario.getPessoaRel().getId() : userDTO.getIdFunc()).get();
                    funcAntigo.setUsuario(null);
                    pessoaRepository.saveAndFlush(funcAntigo);
                    usuario.setPessoaRel(funcionarioRel);
                    funcionarioRel.setUsuario(usuario);
                }
            } else {
                // Novo
                usuario = new Usuario();
                usuario.setUsuNome(userDTO.getUsuNome());
                usuario.setRole(userDTO.getRole());
                usuario.setUsuSenha(passwordEncoder.encode(userDTO.getUsuSenha()));
                usuario.setAtivo(userDTO.isAtivo());
                funcionarioRel.setUsuario(usuario);
                usuario.setPessoaRel(funcionarioRel);
            }

            usuarioRepository.saveAndFlush(usuario);
            pessoaRepository.saveAndFlush(funcionarioRel);
        }
    }


    public Page<UsuarioDTO> listarTodos(Pageable pageable) {
        Page<UsuarioDTO> usuarioDTOS = usuarioRepository.findUsuAndFunc(pageable);
        for (UsuarioDTO usuarioDTO : usuarioDTOS) {
            usuarioDTO.setFuncionario(pessoaRepository.findFuncionarioById(usuarioDTO.getIdFunc()));
        }

        return usuarioDTOS;
    }

    public void remover(Long index) {
        Usuario user = usuarioRepository.findById(index).get();
        Pessoa pes = pessoaRepository.findFuncionarioById(user.getPessoaRel().getId());
        pes.setUsuario(null);
        user.setPessoaRel(null);
        user.setAtivo(false);
        pessoaRepository.saveAndFlush(pes);
        usuarioRepository.saveAndFlush(user);
    }

    public UsuarioDTO buscaUsu(Long index) {
        return usuarioRepository.findUsu(index);
    }
}

