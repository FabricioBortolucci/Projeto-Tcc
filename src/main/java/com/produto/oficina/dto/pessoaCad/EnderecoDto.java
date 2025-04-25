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

    Long id;

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


    public EnderecoDto(Long id, Integer endNumero, String endBairro, String endCep, String endRua, Long cidadeId, boolean endPrincipal) {
        this.id = id;
        this.endNumero = endNumero;
        this.endBairro = endBairro;
        this.endCep = endCep;
        this.endRua = endRua;
        this.cidadeId = cidadeId;
        this.endPrincipal = endPrincipal;
    }
}