package com.gotravel.server.repository;

import com.gotravel.server.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    List<Servicio> findAllByUsuarioIdAndOculto(int idUsuario, String oculto);
    Optional<Servicio> findByIdAndOculto(int id, String oculto);
    List<Servicio> findAllByOcultoAndPublicado(String oculto, String publicado);
    @Query(value = "select s.* from servicio s join contratacion c on s.id_servicio = c.id_servicio where s.oculto = '0' and c.id_etapa = :idEtapa", nativeQuery = true)
    List<Servicio> findAllContratadosByEtapa(int idEtapa);
    @Query(value = "select s.* from servicio s join contratacion c on s.id_servicio = c.id_servicio where s.oculto = '0' and c.id_usuario = :idUsuario", nativeQuery = true)
    List<Servicio> findAllContratadosByUsuario(int idUsuario);
}
