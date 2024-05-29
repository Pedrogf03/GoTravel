package com.gotravel.server.repository;

import com.gotravel.server.model.Etapa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtapaRepository extends JpaRepository<Etapa, Integer> {
    List<Etapa> findAllByViajeId(int id);
}
