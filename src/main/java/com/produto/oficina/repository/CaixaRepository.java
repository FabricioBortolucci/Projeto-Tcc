package com.produto.oficina.repository;

import com.produto.oficina.model.Caixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CaixaRepository extends JpaRepository<Caixa, Long> {

    Caixa findCaixaByDataCadastro(LocalDate dataCadastro);

}
