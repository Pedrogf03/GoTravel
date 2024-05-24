package com.gotravel.server.repository;

import com.gotravel.server.model.Metodopago;
import com.gotravel.server.model.Paypal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetodosPagoRepository extends JpaRepository<Metodopago, Integer> {

    List<Metodopago> findAllByUsuarioId(int id);

}
