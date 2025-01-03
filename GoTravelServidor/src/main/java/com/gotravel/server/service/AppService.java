package com.gotravel.server.service;

import com.gotravel.server.ServerApplication;
import com.gotravel.server.model.*;
import com.gotravel.server.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    public Usuario findUsuarioByServicioId(int idServicio) {
        return usuarioRepository.findByServicioId(idServicio);
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

    public List<Usuario> findAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario findMinimalUserInfoById(int idUsuario) {
        Usuario u = usuarioRepository.findById(idUsuario).orElse(null);

        if(u != null) {
            u.setContrataciones(null);
            u.setMensajesEnviados(null);
            u.setMensajesRecibidos(null);
            u.setPagos(null);
            u.setServicios(null);
            u.setViajes(null);
        }

        return u;

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

    public Etapa findEtapaById(int id) {
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

    public Tiposervicio saveTipoServicio(Tiposervicio tsFromUser) {
        try {
            tsFromUser = tipoServicioRepository.save(tsFromUser);
            return findTipoServicioByNombre(tsFromUser.getNombre());
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            return null;
        }
    }

    private Tiposervicio findTipoServicioByNombre(String nombre) {
        return tipoServicioRepository.findByNombre(nombre);
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
        List<Servicio> servicios = servicioRepository.findAllByUsuarioIdAndOculto(idUsuario, "0");
        List<Servicio> devolver = new ArrayList<>();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter h = DateTimeFormatter.ofPattern("HH:mm:ss");
        for (Servicio s : servicios) {
            if(s.getFechaFinal() != null) {
                LocalDate fechaFinal = LocalDate.parse(s.getFechaFinal(), f);
                if(fechaFinal.isBefore(LocalDate.now())) {
                    s.setPublicado("0");
                }
            } else {
                LocalDate fechaInicio = LocalDate.parse(s.getFechaInicio(), f);
                LocalTime hora = LocalTime.parse(s.getHora(), h);
                if(fechaInicio.isBefore(LocalDate.now())) {
                    s.setPublicado("0");
                } else if (fechaInicio.isEqual(LocalDate.now()) && hora.isBefore(LocalTime.now())) {
                    s.setPublicado("0");
                }
            }
            devolver.add(saveServicio(s));
        }

        return devolver;
    }


    public List<Servicio> findAllServiciosByUsuarioId(int idOtroUsuario) {
        return servicioRepository.findAllByUsuarioId(idOtroUsuario);
    }

    public List<Servicio> findAllServiciosByFechasAndTipoAndPais(LocalDate fechaInicioE, LocalDate fechaFinalE, String tipo, String pais) {
        List<Servicio> servicios = servicioRepository.findAllByOcultoAndPublicadoAndDireccionPais("0", "1", pais);
        List<Servicio> devolver = new ArrayList<>();

        for(Servicio s : servicios) {

            if((tipo.equalsIgnoreCase("transporte") && s.getTipoServicio().getNombre().equalsIgnoreCase("transporte")) || (tipo.equals("estancia"))) {
                LocalDate fechaInicioS = LocalDate.parse(s.getFechaInicio(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                // Si la fecha de inicio del servicio se encuentra entre la de inicio y final de la etapa
                if((fechaInicioS.isEqual(fechaInicioE) || fechaInicioS.isAfter(fechaInicioE)) && (fechaInicioS.isBefore(fechaFinalE) || fechaInicioS.isEqual(fechaFinalE))) {
                    // Si tiene fecha de final y esta también está dentro del rango
                    if(s.getFechaFinal() != null) {
                        LocalDate fechaFinalS = LocalDate.parse(s.getFechaFinal(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                        // Devuelve el servicio
                        if(fechaFinalS.isBefore(fechaFinalE) || fechaFinalS.isEqual(fechaFinalE)) {
                            devolver.add(s);
                        }

                    } else {
                        // Si no tiene fecha de final, devuelve el servicio
                        devolver.add(s);
                    }
                }
            }

        }

        return devolver;

    }

    public List<Servicio> findServiciosContratadosByEtapa(int idEtapa) {
        return servicioRepository.findAllContratadosByEtapa(idEtapa);
    }

    public List<Servicio> findServiciosContratadosByUsuario(int idUsuario) {
        return servicioRepository.findAllContratadosByUsuario(idUsuario);
    }

    @Autowired
    private ImagenRepository imagenRepository;

    public Imagen findFirstImageFromServicioId(int idServicio) {
        return imagenRepository.findFirstByServicioId(idServicio).orElse(null);
    }

    public List<Imagen> findAllImagesFromServicioId(int idServicio) {
        return imagenRepository.findAllByServicioId(idServicio);
    }

    public boolean deleteImagenById(int idImagen) {
        imagenRepository.deleteById(idImagen);
        Imagen i = imagenRepository.findById(idImagen).orElse(null);
        return i == null;
    }

    @Autowired
    private ResenaRepository resenaRepository;

    public List<Resena> findResenasByServicioId(int idServicio) {
        return resenaRepository.findAllByServicioIdAndOculto(idServicio, "0");
    }

    public Resena saveResena(Resena resena) {
        try {
            resena = resenaRepository.save(resena);
            return findResenaById(resena.getId());
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            return null;
        }
    }

    public Resena findResenaById(ResenaId id) {
        return resenaRepository.findById(id).orElse(null);
    }

    public List<Resena> findResenasByUsuarioId(int idOtroUsuario) {
        return resenaRepository.findAllByUsuarioId(idOtroUsuario);
    }

    @Autowired
    private ContratacionRepository contratacionRepository;

    public Contratacion findContratacionByServicioAndUsuario(int idServicio, int idUsuario) {
        return contratacionRepository.findOneByUsuarioIdAndServicioId(idUsuario, idServicio).orElse(null);
    }

    public Contratacion saveContratacion(Contratacion contratacion) {
        try {
            contratacion = contratacionRepository.save(contratacion);
            return findContratacionById(contratacion.getId());
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            return null;
        }
    }

    private Contratacion findContratacionById(String id) {
        return contratacionRepository.findById(id).orElse(null);
    }

    @Autowired
    private MensajeRepository mensajeRepository;

    public List<Mensaje> findAllMensajesByUsuarioId(int idUsuario) {
        return mensajeRepository.findAllMensajesByUsuario(idUsuario);
    }

    public List<Mensaje> findAllMensajesBetweenUsers(int idUsuario, int idOtroUsuario) {
        return mensajeRepository.findAllMensajesBetweenUsers(idUsuario, idOtroUsuario);
    }

    public Mensaje saveMensaje(Mensaje mensaje) {
        try {
            mensaje = mensajeRepository.save(mensaje);
            return findMensajeById(mensaje.getId());
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            return null;
        }
    }

    private Mensaje findMensajeById(Integer id) {
        return mensajeRepository.findById(id).orElse(null);
    }
}
