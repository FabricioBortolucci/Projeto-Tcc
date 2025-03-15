package com.produto.oficina.dto.pessoaCad;

import com.produto.oficina.model.enums.TipoTelefone;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.produto.oficina.model.Telefone}
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TelefoneDto implements Serializable {
    Long id;
    String telNumero;
    TipoTelefone tipo;
    String ddd;
    boolean telPrincipal;
}