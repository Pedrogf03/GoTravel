package com.gotravel.service;

import com.gotravel.model.Usuario;
import com.gotravel.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario findUsuarioById(Integer id) {
        return usuarioRepository.findUsuarioById(id);
    }

    @Override
    public Usuario findUsuarioByEmail(String email) {
        return usuarioRepository.findUsuarioByEmail(email);
    }

    @Override
    public Integer save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

}
