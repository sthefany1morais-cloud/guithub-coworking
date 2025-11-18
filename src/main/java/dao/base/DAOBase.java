package main.java.dao.base;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public abstract class DAOBase<T> implements Persistencia<T> {

    protected static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("coworkingPU");

    protected EntityManager em = emf.createEntityManager();
    private Class<T> clazz;

    public DAOBase(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void salvar(T obj) {
        em.getTransaction().begin();
        em.persist(obj);
        em.getTransaction().commit();
    }

    @Override
    public T buscarPorId(int id) {
        return em.find(clazz, id);
    }

    @Override
    public List<T> carregarTodos() {
        return em.createQuery("FROM " + clazz.getSimpleName(), clazz).getResultList();
    }

    @Override
    public void atualizar(T obj) {
        em.getTransaction().begin();
        em.merge(obj);
        em.getTransaction().commit();
    }

    @Override
    public void remover(int id) {
        T obj = buscarPorId(id);
        if (obj != null) {
            em.getTransaction().begin();
            em.remove(obj);
            em.getTransaction().commit();
        }
    }
}

