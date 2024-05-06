package com.gotravel.server.service;

import com.gotravel.server.ServerApplication;
import com.gotravel.server.model.Usuario;
import com.gotravel.server.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        usuario.setOculto("0");
        try {
            usuario = usuarioRepository.save(usuario);
            return findUsuarioById(usuario.getId());
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            return null;
        }
    }

}
