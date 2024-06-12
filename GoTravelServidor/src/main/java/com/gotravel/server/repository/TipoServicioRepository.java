package com.gotravel.server.repository;

import com.gotravel.server.model.Tiposervicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoServicioRepository extends JpaRepository<Tiposervicio, String> {
    Tiposervicio findByNombre(String nombre);
}
