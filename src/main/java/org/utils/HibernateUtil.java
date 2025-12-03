package org.utils;

import lombok.Getter;
import org.model.Despesa;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {

            Configuration configuration = new Configuration();

            configuration.addAnnotatedClass(Despesa.class);

            return configuration.buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println(" Falha crítica na criação da SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            getSessionFactory().close();
        }
    }
}