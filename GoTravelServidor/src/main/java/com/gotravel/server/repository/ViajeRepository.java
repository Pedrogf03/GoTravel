package com.gotravel.server.repository;

import com.gotravel.server.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Integer> {
    List<Viaje> findAllByUsuarioId(int id);
}
