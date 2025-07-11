package com.produto.oficina.repository;

import com.produto.oficina.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {


    List<Servico> findAllByAtivo(boolean ativo);
}
