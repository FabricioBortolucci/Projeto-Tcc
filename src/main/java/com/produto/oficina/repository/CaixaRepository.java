package com.produto.oficina.repository;

import com.produto.oficina.model.Caixa;
import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.enums.StatusCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaixaRepository extends JpaRepository<Caixa, Long> {

    Optional<Caixa> findTopByStatusOrderByDataFechamentoDesc(StatusCaixa status);

    boolean existsByStatus(StatusCaixa status);

    Caixa findCaixaByStatus(StatusCaixa status);
}
