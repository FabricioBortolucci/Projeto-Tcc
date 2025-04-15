package com.produto.oficina.repository;

import com.produto.oficina.dto.FuncionarioDTO;
import com.produto.oficina.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {


    @Query(value = " select p.pes_id," +
            "       p.pes_nome" +
            " from pessoa p" +
            " where " +
            "  p.pes_tipo = 'FUNCIONARIO' " +
            "  and p.usuario_id is null" +
            "  and p.pes_ativo = true" +
            " order by pes_nome", nativeQuery = true)
    List<FuncionarioDTO> findFuncs();

    Pessoa findFuncionarioById(Long id);

}
