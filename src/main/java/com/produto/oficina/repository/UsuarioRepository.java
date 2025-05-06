package com.produto.oficina.repository;

import com.produto.oficina.dto.UsuarioDTO;
import com.produto.oficina.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsuNome(String usuNome);

    @Query(nativeQuery = true, value = "select u.usu_id, u.usu_nome, u.usu_role, u.pes_id, u.usu_ativo from usuario u" +
            " left join pessoa p on u.usu_id = p.usuario_id")
    Page<UsuarioDTO> findUsuAndFunc(Pageable pageable);

    @Query(nativeQuery = true, value = "select u.usu_id, u.usu_nome, u.usu_role, u.pes_id from usuario u" +
            " left join pessoa p on u.usu_id = p.usuario_id " +
            " where u.usu_id = :index")
    UsuarioDTO findUsu(@Param("index") Long index);

    Usuario findUsuarioByUsuNome(String usuNome);
}
