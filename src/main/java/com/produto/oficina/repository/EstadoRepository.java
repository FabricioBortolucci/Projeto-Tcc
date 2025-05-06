package com.produto.oficina.repository;

import com.produto.oficina.dto.pessoaCad.EstadoDto;
import com.produto.oficina.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {

    @Query(nativeQuery = true, value = "select e.est_id, e.est_nome, e.est_sigla from estado as e where e.est_id = :id")
    EstadoDto buscarPorId(@Param("id") Long id);

}
