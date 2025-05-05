package com.produto.oficina.repository;

import com.produto.oficina.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {


    List<Produto> findByNomeContainingIgnoreCase(String nome);

    @Query(nativeQuery = true, value = "select *" +
            " from produto p" +
            "         left join produto_fornecedor f on p.prod_id = f.produto_id" +
            " where f.fornecedor_id = :fornecedorId;")
    List<Produto> buscaPorFornecedor(@Param("fornecedorId") Long fornecedorId);
}
