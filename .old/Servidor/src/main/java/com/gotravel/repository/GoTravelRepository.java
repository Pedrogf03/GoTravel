package com.gotravel.repository;

import com.gotravel.ServidorApplication;
import com.gotravel.model.Usuario;
import com.gotravel.model.Viaje;
import jakarta.persistence.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class GoTravelRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ServidorApplication.class);

    private final SessionFactory factory;

    {
        Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
        Reflections reflections = new Reflections("com.gotravel.model"); // Busca en este paquete

        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Entity.class)) { // todas las clases que sean @Entity
            configuration.addAnnotatedClass(clazz); // lo añade a la session factory
        }

        factory = configuration.buildSessionFactory();
    }

    public Usuario findUsuarioById(Integer id) {
        Session s = factory.openSession();
        Usuario u = null;
        try {
            Query q = s.createQuery("FROM Usuario WHERE id = :id and oculto = '0'");
            q.setParameter("id", id);
            if(!q.list().isEmpty()) u = (Usuario) q.list().get(0);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return u;
    }

    public Usuario findUsuarioByEmail(String email) {
        Session s = factory.openSession();
        Usuario u = null;
        try {
            Query q = s.createQuery("FROM Usuario WHERE email = :email and oculto = '0'");
            q.setParameter("email", email);
            if(!q.list().isEmpty()) u = (Usuario) q.list().get(0);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return u;
    }

    public Integer save(Object o) {
        Session s = factory.openSession();
        Transaction tx = null;
        Integer idUsuario = null;

        try {
            tx = s.beginTransaction();

            idUsuario = (int) s.save(o);

            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOG.error(e.getMessage());
            return null;
        }
        return idUsuario;
    }


    public List<Viaje> getViajesFromUserId(int idUsuario) {
        Session s = factory.openSession();
        List viajes = null;
        try {
            Query q = s.createQuery("SELECT viajes FROM Usuario WHERE id = :id");
            q.setParameter("id", idUsuario);
            if(!q.list().isEmpty()) viajes = q.list();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return viajes;
    }
}
