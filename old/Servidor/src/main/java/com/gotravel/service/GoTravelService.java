package com.gotravel.service;

import com.gotravel.model.Usuario;
import com.gotravel.model.Viaje;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GoTravelService {

    Usuario findUsuarioById(Integer id);
    Usuario findUsuarioByEmail(String email);
    Integer save(Object o);
    List<Viaje> getViajesFromUserId(int idUsuario);

}
