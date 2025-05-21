package com.produto.oficina.repository;

import com.produto.oficina.model.ContaPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaPagarRepository extends JpaRepository<ContaPagar, Long> {


    List<ContaPagar> findAllByCompra_Id(Long compraId);
}
