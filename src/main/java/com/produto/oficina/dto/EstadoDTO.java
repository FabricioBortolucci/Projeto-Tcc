package com.produto.oficina.dto;

import lombok.Value;

import java.io.Serializable;


@Value
public class EstadoDTO implements Serializable {
    Long id;
    String nome;
    String sigla;
}