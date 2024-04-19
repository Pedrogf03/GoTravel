package com.gotravel.repository;

import jakarta.transaction.Transactional;

import java.util.List;

public interface GoTravelRepository {

    List getAll();

    Object getById(int id);

}
