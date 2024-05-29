package com.gotravel.server.repository;

import com.gotravel.server.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    List<Servicio> findAllByUsuarioIdAndOculto(int idUsuario, String oculto);

    Optional<Servicio> findByIdAndOculto(int id, String oculto);
}
