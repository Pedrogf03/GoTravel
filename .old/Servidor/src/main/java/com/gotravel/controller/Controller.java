package com.gotravel.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.gotravel.model.Usuario;
import com.gotravel.model.Viaje;
import com.gotravel.model.Views;
import com.gotravel.service.GoTravelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controller {

    // ----- USUARIOS -----

    @Autowired
    private GoTravelServiceImpl service;

    @GetMapping("/usuario")
    @JsonView(Views.Usuario.class)
    public Usuario findUsuarioById(@RequestParam Integer id) {
        return service.findUsuarioById(id);
    }

    @GetMapping("/viajes")
    @JsonView(Views.Viaje.class)
    public List<Viaje> getViajesFromUserId(@RequestParam Integer idUsuario) {
        return service.getViajesFromUserId(idUsuario);
    }

    @PostMapping("/saveViaje")
    public Boolean saveViaje(@RequestBody Viaje viaje, @RequestParam Integer usuarioId) {

        viaje.setUsuario(service.findUsuarioById(usuarioId));
        Integer id = service.save(viaje);
        return id != null;

    }

}