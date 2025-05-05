package com.produto.oficina.repository;

import com.produto.oficina.dto.FornecedorDTO;
import com.produto.oficina.dto.FuncionarioDTO;
import com.produto.oficina.dto.pessoaCad.PessoaDto;
import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.enums.PesTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = " select p.pes_id," +
            "       p.pes_nome" +
            " from pessoa p" +
            " where " +
            "  p.pes_tipo = 'FUNCIONARIO' " +
            "  and ((p.usuario_id is null) or (p.usuario_id = :id)) " +
            "  and p.pes_ativo = true" +
            " order by pes_nome", nativeQuery = true)
    List<FuncionarioDTO> findFuncsEAtual(@Param("id") Long id);

    @Query(value = "SELECT p.pes_id, " +
            "       p.pes_nome, " +
            "  t.tel_numero AS telefone " +
            "FROM pessoa p " +
            "LEFT JOIN telefone t ON p.pes_id = t.pessoa_id " +
            "WHERE p.pes_tipo = 'FORNECEDOR' " +
            "  AND p.pes_ativo = true " +
            "ORDER BY p.pes_nome",
            nativeQuery = true)
    List<FornecedorDTO> findFornecedores();

    @Query(value = "SELECT p.pes_id, " +
            "       p.pes_nome," +
            "       p.pes_cpfcnpj," +
            "       p.pes_tipo," +
            "       p.pes_ativo," +
            "       CAST(p.pes_data_nascimento AS date) as pes_data_nascimento," +
            "       p.pes_inscricao_estadual," +
            "       p.pes_rg," +
            "       p.pes_genero," +
            "       p.pes_email," +
            "       p.pes_fsc_jrc" +
            " FROM pessoa p" +
            " WHERE p.pes_id = :id" +
            " ORDER BY p.pes_nome",
            nativeQuery = true)
    PessoaDto findPessoaById(@Param("id") Long id);

    Pessoa findFuncionarioById(Long id);

    List<Pessoa> findAllByPesAtivoAndPesTipo(Boolean pesAtivo, PesTipo pesTipo);
}
