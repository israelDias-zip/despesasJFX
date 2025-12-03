package org;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.model.Despesa;

public class Main {
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unidade-jpa");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Despesa user = new Despesa();
        user.setNome("João Silva");
        user.setEmail("joaozindugrau@gmail.com");
        user.setSenha("123456");
        em.persist(user);
        em.getTransaction().commit();
        em.close();
    }
}
