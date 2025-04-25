package com.produto.oficina.repository;

import com.produto.oficina.dto.pessoaCad.EnderecoDto;
import com.produto.oficina.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    @Query(nativeQuery = true, value = "select e.end_id, e.end_numero, e.end_bairro, e.end_cep, e.end_rua, e.cidade_id, e.end_principal from endereco e where e.pessoa_id = :id ")
    List<EnderecoDto> buscaTodosByPesId(@Param("id") Long pessoaId);
}
