package main.java.dao.base;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public abstract class DAOBase<T> implements Persistencia<T> {

    protected static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("coworkingPU");

    private final Class<T> clazz;

    public DAOBase(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static void persistirDadosGlobais() {
        emf.getCache().evictAll();
    }

    private EntityManager criarEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public void salvar(T obj) {
        EntityManager em = this.criarEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(obj);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public T buscarPorId(int id) {
        EntityManager em = this.criarEntityManager();
        try {
            return em.find(clazz, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> carregarTodos() {
        EntityManager em = this.criarEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + clazz.getSimpleName() + " e", clazz)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void atualizar(T obj) {
        EntityManager em = this.criarEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(obj);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void remover(int id) {
        EntityManager em = this.criarEntityManager();
        try {
            T obj = em.find(clazz, id);
            if (obj != null) {
                em.getTransaction().begin();
                em.remove(obj);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }
}
