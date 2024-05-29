package com.gotravel.server.repository;

import com.gotravel.server.model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, String> {
    Suscripcion findSuscripcionByUsuarioId(int id);
}
