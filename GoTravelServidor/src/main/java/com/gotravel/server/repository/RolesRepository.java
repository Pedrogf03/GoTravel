package com.gotravel.server.repository;

import com.gotravel.server.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Rol, String> {
}
