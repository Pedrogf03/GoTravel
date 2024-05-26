package com.gotravel.server.repository;

import com.gotravel.server.model.DirFacturacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DirFacturacionRepository extends JpaRepository<DirFacturacion, Integer> {
    List<DirFacturacion> findAllByUsuarioId(int idUsuario);
}
