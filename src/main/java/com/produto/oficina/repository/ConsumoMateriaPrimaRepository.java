package com.produto.oficina.repository;

import com.produto.oficina.model.ConsumoMateriaPrima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumoMateriaPrimaRepository extends JpaRepository<ConsumoMateriaPrima, Long> {
}
