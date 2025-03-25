package com.produto.oficina.dto.pessoaCad;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.produto.oficina.model.Endereco}
 */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class EnderecoDto implements Serializable {
    Long id;
    boolean endPrincipal;
    Integer endNumero;
    String endBairro;
    String endCep;
    String endRua;
    CidadeDto cidade;
}