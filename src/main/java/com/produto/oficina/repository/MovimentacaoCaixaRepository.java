package com.produto.oficina.repository;

import com.produto.oficina.dto.pessoaCad.CidadeDto;
import com.produto.oficina.model.Cidade;
import com.produto.oficina.model.MovimentacaoCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoCaixaRepository extends JpaRepository<MovimentacaoCaixa, Long> {


    MovimentacaoCaixa findAllByCaixa_Id(Long caixaId);
}
