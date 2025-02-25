package com.produto.oficina.service.IBGE;

import com.produto.oficina.dto.CidadeDTO;
import com.produto.oficina.dto.EstadoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class IBGEApiService {

    private static final String URL_ESTADOS = "https://servicodados.ibge.gov.br/api/v1/localidades/estados";
    private static final String URL_CIDADES = "https://servicodados.ibge.gov.br/api/v1/localidades/municipios";

    private final RestTemplate restTemplate = new RestTemplate();

    public List<EstadoDTO> obterEstados() {
        EstadoDTO[] estados = restTemplate.getForObject(URL_ESTADOS, EstadoDTO[].class);
        assert estados != null;
        return Arrays.asList(estados);
    }

    public List<CidadeDTO> obterCidades() {
        CidadeDTO[] cidades = restTemplate.getForObject(URL_CIDADES, CidadeDTO[].class);
        assert cidades != null;
        return Arrays.asList(cidades);
    }

}
