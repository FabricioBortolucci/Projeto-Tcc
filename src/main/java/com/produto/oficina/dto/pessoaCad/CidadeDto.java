package com.produto.oficina.dto.pessoaCad;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.produto.oficina.model.Cidade}
 */
@Value
public class CidadeDto implements Serializable {
    Long id;
    String cidNome;
    EstadoDto estado;
}