package com.gotravel.repository;

import com.gotravel.ServidorApplication;
import com.gotravel.model.Usuario;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsuarioRepo implements GoTravelRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ServidorApplication.class);

    private final SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Usuario.class).buildSessionFactory();

    @Override
    @Transactional
    public List getAll() { // Obtener todos los clientes de la base de datos
        Session s = factory.openSession();
        List usuarios = null;
        usuarios = s.createQuery("FROM Usuario").list();
        return usuarios;
    }

    @Override
    @Transactional
    public Object getById(int id) {
        Session s = factory.openSession();
        Usuario u = null;
        Query q = s.createQuery("FROM Usuario WHERE id = :id");
        q.setParameter("id", id);
        if(!q.list().isEmpty()) u = (Usuario) q.list().get(0);
        return u;
    }

    @Transactional
    public Usuario getUsuarioByEmail(String email) {
        Session s = factory.openSession();
        Usuario u = null;
        Query q = s.createQuery("FROM Usuario WHERE email = :email");
        q.setParameter("email", email);
        try {
            u = (Usuario) q.list().get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        return u;
    }

    /*
    public Integer addUsuario(Usuario c) {
        Session s = factory.openSession();
        Transaction tx = null;
        Integer idUsuario = null;

        try {
            tx = s.beginTransaction();

            idUsuario = (int) s.save(c);

            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOG.error(e.getMessage());
            return null;
        }
        return idUsuario;
    }
     */
}
