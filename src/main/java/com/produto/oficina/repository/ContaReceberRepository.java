package com.produto.oficina.repository;

import com.produto.oficina.model.ContaReceber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaReceberRepository extends JpaRepository<ContaReceber, Long> {
    List<ContaReceber> findAllByOrdemServico_Id(Long ordemServicoId);

    List<ContaReceber> findByVendaAvulsaId(Long vendaAvulsaId);
}
