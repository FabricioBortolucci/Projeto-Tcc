package com.produto.oficina.dto.pessoaCad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.produto.oficina.model.Endereco}
 */

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@ToString
public class EnderecoDto implements Serializable {

    @NotNull(message = "O número é obrigatório")
    Integer endNumero;

    @NotBlank(message = "O bairro é obrigatório")
    String endBairro;

    @NotBlank(message = "O CEP é obrigatório")
    String endCep;

    @NotBlank(message = "A rua é obrigatória")
    String endRua;

    @NotNull(message = "A cidade é obrigatória")
    Long cidadeId;

    boolean endPrincipal;
    CidadeDto cidade;
}