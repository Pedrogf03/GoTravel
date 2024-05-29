package com.gotravel.server.repository;

import com.gotravel.server.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findByEmail(String email);

    @Query(value = "select * from usuario where id_usuario = (select id_usuario from servicio where id_servicio = :idServicio)" , nativeQuery = true)
    Usuario findByServicioId(int idServicio);
}
