package com.produto.oficina.repository;

import com.produto.oficina.model.MateriaPrima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaPrimaRepository extends JpaRepository<MateriaPrima, Long> {

    @Query(nativeQuery = true, value = "select *" +
            " from materia_prima mp" +
            "         left join materiaprima_fornecedor f on mp.id = f.mp_id " +
            " where f.fornecedor_id = :fornecedorId;")
    List<MateriaPrima> buscaPorFornecedor(Long fornecedorId);
}
