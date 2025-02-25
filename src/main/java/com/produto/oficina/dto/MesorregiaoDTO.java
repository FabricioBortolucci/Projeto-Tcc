package com.produto.oficina.dto;

import lombok.Value;

import java.io.Serializable;


@Value
public class MesorregiaoDTO implements Serializable {
    Long id;
    String nome;
    EstadoDTO UF;
}