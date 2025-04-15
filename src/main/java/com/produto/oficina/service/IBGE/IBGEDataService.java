package com.produto.oficina.service.IBGE;

import com.produto.oficina.dto.CidadeDTO;
import com.produto.oficina.dto.EstadoDTO;
import com.produto.oficina.model.Cidade;
import com.produto.oficina.model.Estado;
import com.produto.oficina.repository.CidadeRepository;
import com.produto.oficina.repository.EstadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IBGEDataService {

    private final IBGEApiService ibgeApiService;
    private final EstadoRepository estadoRepository;
    private final CidadeRepository cidadeRepository;

    public IBGEDataService(IBGEApiService ibgeApiService, EstadoRepository estadoRepository, CidadeRepository cidadeRepository) {
        this.ibgeApiService = ibgeApiService;
        this.estadoRepository = estadoRepository;
        this.cidadeRepository = cidadeRepository;
    }

    @Transactional
    public void inserirdados() {
        for (EstadoDTO dto : ibgeApiService.obterEstados()) {
            Estado estado = new Estado();
            estado.setId(dto.getId());
            estado.setEstNome(dto.getNome());
            estado.setSigla(dto.getSigla());
            estadoRepository.save(estado);
        }

//        for (CidadeDTO dto : ibgeApiService.obterCidades()) {
//            Cidade cidade = new Cidade();
//            cidade.setId(dto.getId());
//            cidade.setCidNome(dto.getNome());
//
//            Long estadoId = dto.getMicrorregiao().getMesorregiao().getUF().getId();
//            Estado estado = estadoRepository.findById(estadoId);
//            cidade.setEstado(estado);
//            cidadeRepository.save(cidade);
//        }

    }
}
