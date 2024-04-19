package com.gotravel.controller;

import com.gotravel.model.Cliente;
import com.gotravel.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class ClienteController {

    @Autowired
    private ClienteService service;

    @GetMapping("/clientes")
    public List getAll() {
        return service.getAll();
    }

    @GetMapping("/cliente")
    public Object getById(@RequestParam int id) {
        return service.getById(id);
    }

}