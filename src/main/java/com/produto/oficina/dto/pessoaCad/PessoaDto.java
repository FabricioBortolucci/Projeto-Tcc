package com.produto.oficina.dto.pessoaCad;

import com.produto.oficina.model.enums.PesTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    boolean pesAtivo = true;

    @NotNull(message = "A Data é obrigatório")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate pesDataNascimento;
    String pesRg;
    @NotNull(message = "O Genero é obrigatório")
    String pesGenero;
    @NotBlank(message = "O Email é obrigatório")
    String pesEmail;
    String pesFisicoJuridico;
    BigDecimal pesCredito;
    List<EnderecoDto> enderecos;
    List<TelefoneDto> telefones;

    // Listar Todos
    public PessoaDto(Long id, String pesNome, String pesCpfCnpj, String pesTipo, boolean pesAtivo, String pesEmail) {
        this.id = id;
        this.pesNome = pesNome;
        this.pesCpfCnpj = formatCpf(pesCpfCnpj);
        this.pesTipo = PesTipo.fromString(pesTipo);
        this.pesAtivo = pesAtivo;
        this.pesEmail = pesEmail;
    }


    // Visualizacao
    public PessoaDto(Long id, String pesNome, String pesCpfCnpj, String pesTipo, boolean pesAtivo, java.sql.Date pesDataNascimento, String pesRg, String pesGenero, String pesEmail, String pesFisicoJuridico, BigDecimal pesCredito) {
        this.id = id;
        this.pesNome = pesNome;
        this.pesCpfCnpj = pesCpfCnpj;
        this.pesTipo = PesTipo.fromString(pesTipo);
        this.pesAtivo = pesAtivo;
        this.pesDataNascimento = pesDataNascimento != null ? pesDataNascimento.toLocalDate() : null;
        this.pesRg = pesRg;
        this.pesGenero = pesGenero;
        this.pesEmail = pesEmail;
        this.pesFisicoJuridico = pesFisicoJuridico;
        this.pesCredito = pesCredito;
    }

    public String getPesDataNascimentoFormatada() {
        return this.pesDataNascimento != null ? this.pesDataNascimento.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
    }

    public static String formatCpf(String cpfCnpj) {
        if (cpfCnpj.length() != 11) {
            return cpfCnpj.substring(0, 2) + "." + cpfCnpj.substring(2, 5) + "." + cpfCnpj.substring(5, 8) + "/" + cpfCnpj.substring(8, 12) + "-" + cpfCnpj.substring(12, 14);
        }
        return cpfCnpj.substring(0, 3) + "." + cpfCnpj.substring(3, 6) + "." + cpfCnpj.substring(6, 9) + "-" + cpfCnpj.substring(9, 11);
    }
}