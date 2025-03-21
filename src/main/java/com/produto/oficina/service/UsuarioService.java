package com.produto.oficina.service;

import com.produto.oficina.dto.UsuarioDTO;
import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.Usuario;
import com.produto.oficina.model.enums.Role;
import com.produto.oficina.repository.PessoaRepository;
import com.produto.oficina.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        if (userDTO.getId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(userDTO.getId());
            usuario.setUsuNome(userDTO.getUsuNome());
            usuario.setUsuSenha(passwordEncoder.encode(userDTO.getUsuSenha()));

            Optional<Pessoa> funcionarioRel = pessoaRepository.findById(userDTO.getIdFunc());
            if (funcionarioRel.isPresent()) {
                usuario.setPessoaRel(funcionarioRel.get());
                usuarioRepository.save(usuario);
            }
        }
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}

