package com.gotravel.service;

import com.gotravel.model.Cliente;
import com.gotravel.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService implements GoTravelService {

    @Autowired
    private ClienteRepository repo;


    @Override
    public List getAll() {
        return repo.getAll();
    }

    @Override
    public Object getById(int id) {
        return repo.getById(id);
    }
}
