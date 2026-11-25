package pe.edu.upeu.sysventas.repository;

<<<<<<< HEAD
import pe.edu.upeu.sysventas.repository.helper.SqlHelper;

import java.sql.Connection;
import java.sql.SQLException;
=======
>>>>>>> ea262d381fc3af93599530def55fe13f027ea75e
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

<<<<<<< HEAD
public abstract class AbstractJpaRepository<T,ID> extends SqlHelper<T> implements ICrudGenericoRepository<T,ID>{

    protected abstract String getTableName();
    protected abstract String getPkColumn();
    protected abstract T insert(Connection connection, T entity) throws SQLException;
    protected abstract T updateRow(Connection connection, T entity) throws SQLException;
    @Override
    public T save(T entity) {
        try(Connection conn = openConnection()) {
            conn.setAutoCommit(false);
            try {
                T result = insert(conn, entity);
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en guardar: " + e.getMessage(), e);
        }
=======
public abstract class AbstractJpaRepository<T,ID> implements ICrudGenericoRepository<T,ID>{

    protected  final List<T> data=new ArrayList<>();
    protected abstract ID getId(T entity);
    protected abstract void setId(T entity, ID id);
    protected abstract ID generateId();

    @Override
    public T save(T entity) {
        if(getId(entity)==null){
            setId(entity, generateId());
        }
        data.add(entity);
        return entity;
>>>>>>> ea262d381fc3af93599530def55fe13f027ea75e
    }

    @Override
    public T update(T entity) {
<<<<<<< HEAD
        try(Connection conn = openConnection()) {
            conn.setAutoCommit(false);
            try {
                T result = updateRow(conn, entity);
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar: " + e.getMessage(), e);
        }
    }
    @Override
    public Optional<T> findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getPkColumn() + " = ?";
        return executeQueryOne(sql, id);
    }
    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        return executeQuery(sql);
    }
    @Override
    public void deleteById(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getPkColumn() + " = ?";
        executeUpdateStandalone(sql, id);
    }
    @Override
    public boolean existsById(ID id) {
        String sql = "SELECT 1 FROM " + getTableName() + " WHERE " + getPkColumn() + " = ?";
        return executeExists(sql, id);
    }

=======
        ID id=getId(entity);
        for (int i = 0; i < data.size(); i++) {
            T registro=data.get(i);
            if(getId(registro).equals(id)){
                data.set(i, entity);
                return entity;
            }
        }
        throw new RuntimeException("No se encontro el registro con ID "+id);
    }

    @Override
    public Optional<T> findById(ID id) {
        return data.stream()
                .filter(entity->getId(entity).equals(id))
                .findFirst();
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void deleteById(ID id) {
        data.removeIf(entity->getId(entity).equals(id));
    }

    @Override
    public boolean existsById(ID id) {
        return data.stream().anyMatch(entity->getId(entity).equals(id));
    }
>>>>>>> ea262d381fc3af93599530def55fe13f027ea75e
}
