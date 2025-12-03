package org.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.model.Despesa;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure() // <--- O PULO DO GATO: Lê o hibernate.cfg.xml
                    .addAnnotatedClass(Despesa.class) // Adiciona sua classe explicitamente
                    .buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("Falha crítica na criação da SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Getter manual é mais seguro para métodos estáticos utilitários
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}