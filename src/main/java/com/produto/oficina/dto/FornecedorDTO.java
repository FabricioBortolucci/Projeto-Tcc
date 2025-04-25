package com.produto.oficina.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FornecedorDTO implements Serializable {
    Long id;
    String pesNome;
    String telefonePrincipal;
}