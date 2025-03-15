package com.produto.oficina.dto.pessoaCad;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.produto.oficina.model.Pessoa}
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PessoaDto implements Serializable {
    Long id;
    String pesNome;
    String pesCpfCnpj;
    boolean pesCliente;
    boolean pesFuncionario;
    boolean pesAtivo;
    String endPrincipal;
    String telPrincipal;
    List<EnderecoDto> enderecos;
    List<TelefoneDto> telefones;

    public PessoaDto(Long id, String pesNome, String pesCpfCnpj, boolean pesCliente, boolean pesAtivo) {
    }
}