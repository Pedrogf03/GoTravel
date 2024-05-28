package com.gotravel.server.repository;

import com.gotravel.server.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    List<Servicio> findAllByUsuarioId(int idUsuario);
}
