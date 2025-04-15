package com.produto.oficina.service;

import com.produto.oficina.dto.pessoaCad.EstadoDto;
import com.produto.oficina.model.Estado;
import com.produto.oficina.repository.EstadoRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class EstadoService {


    private final EstadoRepository estadoRepository;

    public EstadoService(EstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    public List<EstadoDto> findAll() {
        return estadoRepository.findAll().stream().map(estado -> {
            EstadoDto estadoDto = new EstadoDto();
            estadoDto.setId(estado.getId());
            estadoDto.setEstNome(estado.getEstNome());
            return estadoDto;
        }).sorted(Comparator.comparing(EstadoDto::getEstNome)).toList();
    }

    public Optional<Estado> buscarPorId(Long id) {
       return estadoRepository.findById(id);
    }


    public EstadoDto buscarPorIdDto(Long id) {
       return estadoRepository.buscarPorId(id);
    }
}
