package com.produto.oficina.repository;

import com.produto.oficina.model.FornecedorProduto;
import com.produto.oficina.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FornecedorProdutoRepository extends JpaRepository<FornecedorProduto, Long> {

    List<FornecedorProduto> findByFornecedorIdAndAtivoTrue(Long fornecedorId);

}
