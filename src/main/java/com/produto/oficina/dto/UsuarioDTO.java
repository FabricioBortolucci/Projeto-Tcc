package com.produto.oficina.dto;

import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.enums.Role;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.produto.oficina.model.Pessoa}
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UsuarioDTO implements Serializable {
    Long id;
    String usuNome;
    String usuSenha;
    Role role;
    String roleName;
    Long idFunc;
    Pessoa funcionario;
    boolean ativo = true;




    public UsuarioDTO(Long id, String usuNome, String roleName, Long idFunc) {
        this.id = id;
        this.usuNome = usuNome;
        this.roleName = roleName;
        this.idFunc = idFunc;
    }

    public UsuarioDTO(Long id, String usuNome, String roleName, Long idFunc, boolean ativo) {
        this.id = id;
        this.usuNome = usuNome;
        this.roleName = roleName;
        this.idFunc = idFunc;
        this.ativo = ativo;
    }
}