package com.produto.oficina.repository;

import com.produto.oficina.model.Produto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {


    List<Produto> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT COUNT(p) FROM Produto p WHERE p.estoque <= 3")
    long countProdutosEmEstoqueBaixo();


    @Query("SELECT p FROM Produto p WHERE p.estoque <= 3 ORDER BY p.estoque DESC, p.nome ASC")
    List<Produto> findProdutosComEstoqueBaixo(Pageable topAlertas);

    List<Produto> findAllByAtivo(Boolean ativo);
}
