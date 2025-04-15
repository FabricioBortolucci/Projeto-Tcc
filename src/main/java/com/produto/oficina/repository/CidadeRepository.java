package com.produto.oficina.repository;

import com.produto.oficina.dto.pessoaCad.CidadeDto;
import com.produto.oficina.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long> {

    @Query(nativeQuery = true, value = "select c.cid_id, c.cid_nome from cidade c")
    List<CidadeDto> buscaIdEnome();

    @Query(nativeQuery = true, value = "select c.cid_id, c.cid_nome from cidade c where c.estado_id = :estadoId")
    List<CidadeDto> findCidadesByEstadoId(@Param("estadoId") Long estadoId);

    @Query(nativeQuery = true, value = "select c.cid_id, c.cid_nome, c.estado_id from cidade c where c.cid_id = :id")
    CidadeDto findCidadeById(@Param("id") Long id);

}
