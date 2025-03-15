package com.produto.oficina.repository;

import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Pessoa, String> {
}
