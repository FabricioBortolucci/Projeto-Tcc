package com.produto.oficina.dto.pessoaCad;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.produto.oficina.model.Estado}
 */
@Value
public class EstadoDto implements Serializable {
    Long id;
    String estNome;
    String sigla;
}