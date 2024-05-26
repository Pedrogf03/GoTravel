package com.gotravel.server.repository;

import com.gotravel.server.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Rol, String> {
}
