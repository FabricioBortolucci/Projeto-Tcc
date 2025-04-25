package com.produto.oficina.repository;

import com.produto.oficina.dto.pessoaCad.TelefoneDto;
import com.produto.oficina.model.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelefoneRepository extends JpaRepository<Telefone, Long> {
    Telefone findTelefoneByTelPrincipalIsTrue();

    @Query(nativeQuery = true, value = "select t.id, t.tel_numero, t.tel_tipo, t.tel_principal from telefone t where t.pessoa_id = :pessoaId")
    List<TelefoneDto> buscaTodosByPesId(@Param("pessoaId") Long pessoaId);
}
