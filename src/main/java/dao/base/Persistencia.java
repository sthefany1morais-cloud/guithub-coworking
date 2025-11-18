package main.java.dao.base;

import java.util.List;

public interface Persistencia<T> {
    void salvar(T obj);
    T buscarPorId(int id);
    List<T> carregarTodos();
    void atualizar(T obj);
    void remover(int id);
}
