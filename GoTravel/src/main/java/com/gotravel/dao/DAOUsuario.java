package com.gotravel.dao;

import com.gotravel.entity.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public class DAOUsuario {

    private SessionFactory factory;

    public DAOUsuario() {

        factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Usuario.class).buildSessionFactory();

    }

    public Usuario getUsuario(String email) {

        Session sesion = factory.openSession();
        Transaction tx = null;
        List<Usuario> user;

        try {

            tx = sesion.beginTransaction();

            Query q = sesion.createQuery("FROM Usuario WHERE email = :email");
            q.setParameter("email", email);

            user = q.list();

            if(user.size() == 1) {
                return user.get(0);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            sesion.close();
        }

        return null;

    }

    public Integer addUsuario(String usuario, String email, String passwd) {

        Session sesion = factory.openSession();
        Transaction tx = null;
        Integer idUsuario = null;

        try {
            tx = sesion.beginTransaction();
            idUsuario = (Integer) sesion.save(new Usuario(usuario, email, passwd));
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            sesion.close();
        }

        return idUsuario;

    }

}
