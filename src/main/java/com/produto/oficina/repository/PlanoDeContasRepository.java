package com.produto.oficina.repository;

import com.produto.oficina.model.PlanoDeContas;
import com.produto.oficina.model.enums.NaturezaContaPlanoContas;
import com.produto.oficina.model.enums.TipoContaPlanoContas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoDeContasRepository extends JpaRepository<PlanoDeContas, Long> {
    List<PlanoDeContas> findAllByTipoContaOrderByCodigoAsc(TipoContaPlanoContas tipoConta);

    boolean existsByCodigo(String codigo);

    Optional<PlanoDeContas> findByCodigoAndIdNot(String codigo, Long id);

    Page<PlanoDeContas> findByCodigoContainingIgnoreCaseOrDescricaoContainingIgnoreCase(
            String filtroCodigo, String filtroDescricao, Pageable pageable);

    List<PlanoDeContas> findAllByTipoContaAndNaturezaContaInOrderByCodigoAsc(
            TipoContaPlanoContas tipo,
            List<NaturezaContaPlanoContas> naturezas
    );

    List<PlanoDeContas> findAllByNaturezaContaOrderByCodigoAsc(NaturezaContaPlanoContas naturezaContas);

    List<PlanoDeContas> findAllByCodigoStartsWithOrderByCodigoAsc(String codigo);

    Optional<PlanoDeContas> findByCodigo(String s);
}
