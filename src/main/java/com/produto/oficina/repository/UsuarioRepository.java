package com.produto.oficina.repository;

import com.produto.oficina.dto.UsuarioDTO;
import com.produto.oficina.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByUsuNome(String usuNome);

    @Query(nativeQuery = true, value = "select u.usu_id, u.usu_nome, u.usu_role, u.pes_id from usuario u" +
            " left join pessoa p on u.usu_id = p.usuario_id")
    Page<UsuarioDTO> findUsuAndFunc(Pageable pageable);

}
