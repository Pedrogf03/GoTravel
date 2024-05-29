package com.gotravel.server.repository;

import com.gotravel.server.model.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Integer> {
    Optional<Imagen> findFirstByServicioId(int idServicio);

    List<Imagen> findAllByServicioId(int idServicio);
}
