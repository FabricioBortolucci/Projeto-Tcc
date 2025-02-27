package com.produto.oficina.service;

import com.produto.oficina.model.Usuario;
import com.produto.oficina.model.enums.Role;
import com.produto.oficina.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public void cadastrarUsuario(String nome, String senha, Role role) {
        Usuario usuario = new Usuario();
        usuario.setUsuNome(nome);
        usuario.setUsuSenha(passwordEncoder.encode(senha));
        usuario.setRole(role);
        usuarioRepository.save(usuario);
    }
}

