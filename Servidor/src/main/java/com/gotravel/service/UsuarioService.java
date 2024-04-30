package com.gotravel.service;

import com.gotravel.repository.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements GoTravelService {

    @Autowired
    private UsuarioRepo repo;

    @Override
    public List getAll() {
        return repo.getAll();
    }

    @Override
    public Object getById(int id) {
        return repo.getById(id);
    }
}
