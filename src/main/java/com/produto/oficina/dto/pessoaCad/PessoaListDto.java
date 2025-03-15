package com.produto.oficina.dto.pessoaCad;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.produto.oficina.model.Pessoa}
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PessoaListDto implements Serializable {
    Long id;
    String pesNome;
    String pesCpfCnpj;
    String enderecoPrincipal;
    String telefonePrincipal;
    boolean ativo;
}