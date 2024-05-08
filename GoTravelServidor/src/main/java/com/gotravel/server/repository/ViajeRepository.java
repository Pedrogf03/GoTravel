package com.gotravel.server.repository;

import com.gotravel.server.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Integer> {
    List<Viaje> findAllByUsuarioId(int id);

    @Query(value = "SELECT * FROM Viaje WHERE id_usuario = :id AND fecha_inicio >= CURRENT_DATE ORDER BY fecha_inicio ASC", nativeQuery = true)
    Viaje findProximoViajeByUsuarioId(@Param("id") int id);

    @Query(value = "SELECT * FROM Viaje WHERE id_usuario = :id AND fecha_inicio <= CURRENT_DATE AND (fecha_fin >= CURRENT_DATE OR fecha_fin IS NULL) ORDER BY fecha_inicio ASC", nativeQuery = true)
    Viaje findViajeActualByUsuarioId(@Param("id") int idUsuario);
}
