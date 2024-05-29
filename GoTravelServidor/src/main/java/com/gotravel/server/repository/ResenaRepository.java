package com.gotravel.server.repository;

import com.gotravel.server.model.Resena;
import com.gotravel.server.model.ResenaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, ResenaId> {
    List<Resena> findAllByServicioId(int idServicio);
}
