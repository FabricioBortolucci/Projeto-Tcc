package com.produto.oficina.repository;

import com.produto.oficina.model.LancamentoFinanceiro;
import com.produto.oficina.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LancamentoFinanceiroRepository extends JpaRepository<LancamentoFinanceiro, Long> {

}
