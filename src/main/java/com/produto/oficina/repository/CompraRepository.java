package com.produto.oficina.repository;

import com.produto.oficina.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {


    Compra findCompraByIdOrderByIdAsc(Long id);
}
