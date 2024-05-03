package com.gotravel.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.gotravel.model.Views;
import com.gotravel.service.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class Controller {

    // ----- USUARIOS -----

    @Autowired
    private UsuarioServiceImpl service;

    @GetMapping("/{idUsuario}")
    @JsonView(Views.Usuario.class)
    public Object findUsuarioById(@PathVariable Integer idUsuario) {
        return service.findUsuarioById(idUsuario);
    }

}