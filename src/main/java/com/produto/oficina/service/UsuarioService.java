package com.produto.oficina.service;

import com.produto.oficina.dto.UsuarioDTO;
import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.Usuario;
import com.produto.oficina.model.enums.Role;
import com.produto.oficina.repository.PessoaRepository;
import com.produto.oficina.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            Usuario usuario = new Usuario();
            usuario.setUsuNome(userDTO.getUsuNome());
            usuario.setRole(userDTO.getRole());
            usuario.setUsuSenha(passwordEncoder.encode(userDTO.getUsuSenha()));

            Optional<Pessoa> funcionarioRel = pessoaRepository.findById(userDTO.getIdFunc());
            if (funcionarioRel.isPresent()) {
                funcionarioRel.get().setUsuario(usuario);
                usuario.setPessoaRel(funcionarioRel.get());
                usuarioRepository.save(usuario);
            }
        }
    }

    public Page<UsuarioDTO> listarTodos(Pageable pageable) {
        Page<UsuarioDTO> usuarioDTOS = usuarioRepository.findUsuAndFunc(pageable);
        for (UsuarioDTO usuarioDTO : usuarioDTOS) {
            usuarioDTO.setFuncionario(pessoaRepository.findFuncionarioById(usuarioDTO.getIdFunc()));
        }

        return usuarioDTOS;
    }
}

