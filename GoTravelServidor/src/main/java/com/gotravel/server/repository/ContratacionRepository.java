package com.gotravel.server.repository;

import com.gotravel.server.model.Contratacion;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ContratacionRepository extends JpaRepository<Contratacion, String> {

    Optional<Contratacion> findOneByUsuarioIdAndServicioId(int usuarioId, int servicioId);

}
