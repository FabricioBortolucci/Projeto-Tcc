package com.produto.oficina.repository;

import com.produto.oficina.model.MovimentacaoCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoCaixaRepository extends JpaRepository<MovimentacaoCaixa, Long> {


    List<MovimentacaoCaixa> findAllByCaixa_Id(Long caixaId);

    List<MovimentacaoCaixa> findAllByCaixa_IdOrderByIdAsc(Long caixaId);

    List<MovimentacaoCaixa> findAllByCaixa_IdOrderByIdDesc(Long caixaId);
}
