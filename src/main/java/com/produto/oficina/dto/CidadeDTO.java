package com.produto.oficina.dto;

import lombok.Value;

import java.io.Serializable;


@Value
public class CidadeDTO implements Serializable {
    Long id;
    String nome;
    MicrorregiaoDTO microrregiao;
}