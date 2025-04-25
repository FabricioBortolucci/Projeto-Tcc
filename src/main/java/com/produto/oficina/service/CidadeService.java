package com.produto.oficina.service;

import com.produto.oficina.dto.pessoaCad.CidadeDto;
import com.produto.oficina.dto.pessoaCad.EstadoDto;
import com.produto.oficina.model.Cidade;
import com.produto.oficina.repository.CidadeRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CidadeService {


    private final CidadeRepository cidadeRepository;
    private final EstadoService estadoService;

    public CidadeService(CidadeRepository cidadeRepository, EstadoService estadoService) {
        this.cidadeRepository = cidadeRepository;
        this.estadoService = estadoService;
    }

    public List<CidadeDto> listar() {
        return cidadeRepository.buscaIdEnome().stream().sorted(Comparator.comparing(CidadeDto::getCidNome)).toList();
    }

    public List<CidadeDto> findCidadesByEstadoId(Long id) {
        return cidadeRepository.findCidadesByEstadoId(id);
    }


    public CidadeDto findCidadeEestadoById(Long id) {
        CidadeDto cidadeDto = cidadeRepository.findCidadeById(id);
        cidadeDto.setEstado(estadoService.buscarPorIdDto(cidadeDto.getEstadoId()));
        return cidadeDto;
    }

    public CidadeDto findCidadeById(Long id) {
        return cidadeRepository.findCidadeById(id);
    }

    public Cidade buscaCidadePorId( Long cidadeId) {
        return cidadeRepository.findById(cidadeId).get();
    }
}
