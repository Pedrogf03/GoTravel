package com.gotravel.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.gotravel.model.Views;
import com.gotravel.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping("/usuarios")
    @JsonView(Views.UsuarioView.class)
    public List getAll() {
        return service.getAll();
    }

    @GetMapping("/usuario")
    @JsonView(Views.UsuarioView.class)
    public Object getById(@RequestParam int id) {
        return service.getById(id);
    }

}