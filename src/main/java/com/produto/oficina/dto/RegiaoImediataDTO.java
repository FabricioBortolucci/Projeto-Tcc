package com.produto.oficina.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.io.Serializable;


@Value
public class RegiaoImediataDTO implements Serializable {
    Long id;
    String nome;
    @JsonProperty("regiao-intermediaria")
    RegiaoIntermediariaDTO regiaoIntermediaria;
}