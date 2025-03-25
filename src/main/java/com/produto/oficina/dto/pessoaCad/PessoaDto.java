package com.produto.oficina.dto.pessoaCad;

import com.produto.oficina.model.enums.PesTipo;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.produto.oficina.model.Pessoa}
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class PessoaDto implements Serializable {
    Long id;
    String pesNome;
    String pesCpfCnpj;
    PesTipo pesTipo;
    boolean pesAtivo;
    LocalDate pesDataNascimento;
    String pesInscricaoEstadual;
    String pesRg;
    String pesGenero;
    String pesEmail;
    String pesFisicoJuridico;
    List<EnderecoDto> enderecos;
    List<TelefoneDto> telefones;

    public PessoaDto(Long id, String pesNome, String pesCpfCnpj, PesTipo pesTipo, boolean pesAtivo, String pesEmail) {
        this.id = id;
        this.pesNome = pesNome;
        this.pesCpfCnpj = pesCpfCnpj;
        this.pesTipo = pesTipo;
        this.pesAtivo = pesAtivo;
        this.pesEmail = pesEmail;
    }
}