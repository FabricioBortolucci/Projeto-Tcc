package com.produto.oficina.repository;

import com.produto.oficina.model.Caixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CaixaRepository extends JpaRepository<Caixa, Long> {


    Caixa findCaixaByFechadoAndDataCadastro(boolean b, LocalDateTime dataCadastro);

    Caixa findCaixaByFechadoAndDataCadastroBetween(boolean fechado, LocalDateTime dataCadastroAfter, LocalDateTime dataCadastroBefore);

    Caixa findFirstByFechadoTrueOrderByDataCadastroDesc();

    List<Caixa> findCaixaById(Long id);
}
