package com.gotravel.server.repository;

import com.gotravel.server.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {

    @Query(value = "SELECT * FROM (SELECT m.* FROM mensaje m JOIN usuario u1 ON m.id_emisor = u1.id_usuario JOIN usuario u2 ON m.id_receptor = u2.id_usuario WHERE (m.id_emisor = :idUsuarioActual AND m.id_receptor = :idOtroUsuario) OR (m.id_emisor = :idOtroUsuario AND m.id_receptor = :idUsuarioActual) ORDER BY m.fecha DESC, m.hora DESC LIMIT 100) AS sub ORDER BY sub.fecha, sub.hora", nativeQuery = true)
    List<Mensaje> findAllMensajesBetweenUsers(int idUsuarioActual, int idOtroUsuario);

    @Query(value = "SELECT *\n" +
            "FROM (\n" +
            "    SELECT *, ROW_NUMBER() OVER (PARTITION BY LEAST(id_emisor, id_receptor), GREATEST(id_emisor, id_receptor) ORDER BY fecha DESC, hora DESC) as rn\n" +
            "    FROM mensaje\n" +
            ") t\n" +
            "WHERE rn = 1\n" +
            "AND (id_emisor = :idUsuario OR id_receptor = :idUsuario)", nativeQuery = true)
    List<Mensaje> findAllMensajesByUsuario(int idUsuario);

}
