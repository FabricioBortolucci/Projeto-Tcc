package com.produto.oficina.dto;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.produto.oficina.model.Pessoa}
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FuncionarioDTO implements Serializable {
    Long id;
    String pesNome;
}