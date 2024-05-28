package com.gotravel.server.service;

import com.gotravel.server.ServerApplication;
import com.gotravel.server.model.Suscripcion;
import com.gotravel.server.model.*;
import com.gotravel.server.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppService {

    private final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario findUsuarioById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario findUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario saveUsuario(Usuario usuario) {
        try {
            usuario = usuarioRepository.save(usuario);
            return findUsuarioById(usuario.getId());
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            return null;
        }
    }

    public byte[] getFotoFromUsuarioId(Integer id) {
        return usuarioRepository.findById(id).get().getFoto();
    }

    @Autowired
    private ViajeRepository viajeRepository;

    public Viaje findViajeById(Integer id) {
        return viajeRepository.findById(id).orElse(null);
    }

    public List<Viaje> findViajesByUsuarioId(int idUsuario) {
        return viajeRepository.findAllByUsuarioId(idUsuario);
    }

    public Viaje findProximoViajeByUsuarioId(int idUsuario) {
        return viajeRepository.findProximoViajeByUsuarioId(idUsuario);
    }

    public Viaje findViajeActualByUsuarioId(int idUsuario) {
        return viajeRepository.findViajeActualByUsuarioId(idUsuario);
    }

    public Viaje saveViaje(Viaje viaje) {
        try {
            viaje = viajeRepository.save(viaje);
            return findViajeById(viaje.getId());
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            return null;
        }
    }

    @Autowired
    private EtapaRepository etapaRepository;

    public List<Etapa> findEtapasByViajeId(int idViaje) {
        return etapaRepository.findAllByViajeId(idViaje);
    }

    private Etapa findEtapaById(int id) {
        return etapaRepository.findById(id).orElse(null);
    }

    public Etapa saveEtapa(Etapa etapa) {
        try {
            etapa = etapaRepository.save(etapa);
            return findEtapaById(etapa.getId());
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            return null;
        }
    }

    @Autowired
    private RolesRepository rolesRepository;

    public Rol findRol(String rol) {
        return rolesRepository.findById(rol).orElse(null);
    }

    @Autowired
    private PagoRepository pagoRepository;

    public Pago savePago(Pago pago) {
        try {
            pago = pagoRepository.save(pago);
            return findPagoById(pago.getId());
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            return null;
        }
    }

    private Pago findPagoById(int id) {
        return pagoRepository.findById(id).orElse(null);
    }

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    public Suscripcion saveSuscripcion(Suscripcion suscripcion) {
        try {
            suscripcion = suscripcionRepository.save(suscripcion);
            return findSuscripcionById(suscripcion.getId());
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            return null;
        }
    }

    public Suscripcion findSuscripcionById(String id) {
        return suscripcionRepository.findById(id).orElse(null);
    }

    public Suscripcion findSuscripcionByUsuarioId(int idUsuario) {
        return suscripcionRepository.findSuscripcionByUsuarioId(idUsuario);
    }

    @Autowired
    private TipoServicioRepository tipoServicioRepository;

    public List<Tiposervicio> findAllTiposServicio() {
        return tipoServicioRepository.findAll();
    }

    @Autowired
    private ServicioRepository servicioRepository;

    public Servicio saveServicio(Servicio servicio) {
        try {
            servicio = servicioRepository.save(servicio);
            return findServicioById(servicio.getId());
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            return null;
        }
    }

    public Servicio findServicioById(int id) {
        return servicioRepository.findById(id).orElse(null);
    }

    public List<Servicio> findServiciosByUsuarioId(int idUsuario) {
        return servicioRepository.findAllByUsuarioId(idUsuario);
    }
}
