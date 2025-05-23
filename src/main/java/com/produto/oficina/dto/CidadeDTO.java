package com.produto.oficina.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.io.Serializable;


@Value
public class CidadeDTO implements Serializable {
    Long id;
    String nome;
    MicrorregiaoDTO microrregiao;
    @JsonProperty("regiao-imediata")
    RegiaoImediataDTO regiaoImediata;
}