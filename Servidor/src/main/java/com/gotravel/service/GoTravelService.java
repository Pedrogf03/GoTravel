package com.gotravel.service;

import com.gotravel.model.Cliente;

import java.util.List;

public interface GoTravelService {

    List getAll();

    Object getById(int id);

}
