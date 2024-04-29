package com.gotravel.repository;

import java.util.List;

public interface GoTravelRepository {

    List getAll();
    Object getById(int id);

}
