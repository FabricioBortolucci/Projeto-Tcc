package com.produto.oficina.repository;

import com.produto.oficina.model.PlanoDeContas;
import com.produto.oficina.model.enums.TipoContaPlanoContas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoDeContasRepository extends JpaRepository<PlanoDeContas, Long> {
    // Para o <select> de contas pai, busca apenas as contas que podem ser pai (Sintéticas)
    List<PlanoDeContas> findAllByTipoContaOrderByCodigoAsc(TipoContaPlanoContas tipoConta);

    // Verifica se um código já existe (usado na criação)
    boolean existsByCodigo(String codigo);

    // Verifica se um código já existe em outro registro (usado na edição)
    Optional<PlanoDeContas> findByCodigoAndIdNot(String codigo, Long id);

    Page<PlanoDeContas> findByCodigoContainingIgnoreCaseOrDescricaoContainingIgnoreCase(
            String filtroCodigo, String filtroDescricao, Pageable pageable);
}
