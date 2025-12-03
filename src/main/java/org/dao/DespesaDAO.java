package org.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.model.Despesa;
import org.utils.HibernateUtil;

import java.util.List;

public class DespesaDAO {

    public void salvarDespesa(Despesa despesa) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(despesa); // Salva no banco
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Desfaz se der erro
            }
            e.printStackTrace();
        }
    }

    public List<Despesa> listarDespesas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL: "from Despesa" equivale a "SELECT * FROM despesas"
            return session.createQuery("from Despesa", Despesa.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void atualizarDespesa(Despesa despesa) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(despesa); // Atualiza os dados baseado no ID
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void excluirDespesa(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Primeiro busca o objeto para garantir que ele existe
            Despesa despesa = session.find(Despesa.class, id);

            if (despesa != null) {
                session.remove(despesa); // Remove do banco
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public double calcularTotalDespesas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT SUM(d.valor) FROM Despesa d";

            Double total = session.createQuery(hql, Double.class).uniqueResult();


            return total != null ? total : 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}