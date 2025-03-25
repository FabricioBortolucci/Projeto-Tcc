package com.produto.oficina.dto.pessoaCad;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.produto.oficina.model.Estado}
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EstadoDto implements Serializable {
    Long id;
    String estNome;
    String sigla;
}