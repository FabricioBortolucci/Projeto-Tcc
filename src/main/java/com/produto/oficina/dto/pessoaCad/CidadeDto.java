package com.produto.oficina.dto.pessoaCad;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.produto.oficina.model.Cidade}
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CidadeDto implements Serializable {
    Long id;
    String cidNome;
    EstadoDto estado;
}