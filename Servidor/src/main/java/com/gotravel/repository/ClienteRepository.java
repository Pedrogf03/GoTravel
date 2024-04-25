package com.gotravel.repository;

import com.gotravel.ServidorApplication;
import com.gotravel.model.Cliente;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClienteRepository implements GoTravelRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ServidorApplication.class);

    private final SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Cliente.class).buildSessionFactory();

    @Override
    @Transactional
    public List getAll() { // Obtener todos los clientes de la base de datos
        Session s = factory.openSession();
        List clientes = null;
        clientes = s.createQuery("FROM Cliente").list();
        return clientes;
    }

    @Override
    @Transactional
    public Object getById(int id) {
        Session s = factory.openSession();
        Cliente c = null;
        Query q = s.createQuery("FROM Cliente WHERE id = :id");
        q.setParameter("id", id);
        c = (Cliente) q.list().get(0);
        return c;
    }

    public Cliente getClienteByEmail(String email) {
        Session s = factory.openSession();
        Cliente c = null;
        Query q = s.createQuery("FROM Cliente WHERE email = :email");
        q.setParameter("email", email);
        try {
            c = (Cliente) q.list().get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        return c;
    }

    public Integer addCliente(Cliente c) {
        Session s = factory.openSession();
        Transaction tx = null;
        Integer idCliente = null;

        try {
            tx = s.beginTransaction();

            idCliente = (int) s.save(c);

            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOG.error(e.getMessage());
            return null;
        }
        return idCliente;
    }
}
