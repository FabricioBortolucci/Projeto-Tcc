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
    Long estadoId;
    EstadoDto estado;

    public CidadeDto(Long id, String cidNome) {
        this.id = id;
        this.cidNome = cidNome;
    }

    public CidadeDto(Long id, String cidNome, Long estadoId) {
        this.id = id;
        this.cidNome = cidNome;
        this.estadoId = estadoId;
    }
}