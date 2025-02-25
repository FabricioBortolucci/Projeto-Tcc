package com.produto.oficina.dto;

import lombok.Value;

import java.io.Serializable;


@Value
public class MicrorregiaoDTO implements Serializable {
    Long id;
    String nome;
    MesorregiaoDTO mesorregiao;
}