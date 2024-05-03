package com.gotravel.service;

import com.gotravel.model.Usuario;
import org.springframework.stereotype.Service;

@Service
public interface UsuarioService {

    Usuario findUsuarioById(Integer id);
    Usuario findUsuarioByEmail(String email);
    Integer save(Usuario usuario);

}
