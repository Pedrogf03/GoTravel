package com.gotravel.server.repository;

import com.gotravel.server.model.Etapa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EtapaRepository extends JpaRepository<Etapa, Integer> {
    List<Etapa> findAllByViajeId(int id);
}
