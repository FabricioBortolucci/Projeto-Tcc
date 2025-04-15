package com.produto.oficina.dto.pessoaCad;

import com.produto.oficina.model.enums.PesTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "O Nome é obrigatório")
    String pesNome;
    @NotBlank(message = "O CPF é obrigatório")
    String pesCpfCnpj;
    @NotNull(message = "O Tipo é obrigatório")
    PesTipo pesTipo;
    boolean pesAtivo;

    @NotNull(message = "A Data é obrigatório")
    LocalDate pesDataNascimento;
    String pesInscricaoEstadual;
    String pesRg;
    @NotNull(message = "O Genero é obrigatório")
    String pesGenero;
    @NotBlank(message = "O Email é obrigatório")
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