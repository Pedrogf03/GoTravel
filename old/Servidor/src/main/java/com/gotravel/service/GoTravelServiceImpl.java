package com.gotravel.service;

import com.gotravel.model.Usuario;
import com.gotravel.model.Viaje;
import com.gotravel.repository.GoTravelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoTravelServiceImpl implements GoTravelService {

    private final GoTravelRepository repository;

    @Autowired
    public GoTravelServiceImpl(GoTravelRepository usuarioRepository) {
        this.repository = usuarioRepository;
    }

    @Override
    public Usuario findUsuarioById(Integer id) {
        return repository.findUsuarioById(id);
    }

    @Override
    public Usuario findUsuarioByEmail(String email) {
        return repository.findUsuarioByEmail(email);
    }

    @Override
    public Integer save(Object o) {
        return repository.save(o);
    }

    @Override
    public List<Viaje> getViajesFromUserId(int idUsuario) {
        return repository.getViajesFromUserId(idUsuario);
    }

}
