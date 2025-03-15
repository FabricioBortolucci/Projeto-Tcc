package com.produto.oficina.dto;

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
    Long idFunc;

}