package com.produto.oficina.dto.pessoaCad;

import com.produto.oficina.model.enums.TipoTelefone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.produto.oficina.model.Telefone}
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
public class TelefoneDto implements Serializable {
    Long id;
    @NotBlank(message = "O número é obrigatório")
    String telNumero;

    @NotNull(message = "O Tipo é obrigatório")
    TipoTelefone tipo;
    String ddd;
    boolean telPrincipal;
}