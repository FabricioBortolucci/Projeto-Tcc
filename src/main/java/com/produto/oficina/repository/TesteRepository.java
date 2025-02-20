package com.produto.oficina.repository;

import com.produto.oficina.entity.Teste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TesteRepository extends JpaRepository<Teste, Long> {
}
