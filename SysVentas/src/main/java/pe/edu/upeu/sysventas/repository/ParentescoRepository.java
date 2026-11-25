package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Parentesco;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParentescoRepository extends AbstractJpaRepository<Parentesco, Integer> {

    @Override
    protected String getTableName() {
        return "Parentesco";
    }

    @Override
    protected String getPkColumn() {
        return "idParentesco";
    }

    @Override
    protected Parentesco insert(Connection connection, Parentesco entity) throws SQLException {
        if (entity.getIdParentesco() != null && entity.getIdParentesco() > 0) {
            executeUpdate(connection,
                    "INSERT INTO Parentesco(idParentesco, nombre) VALUES(?, ?)",
                    entity.getIdParentesco(), entity.getNombre());
        } else {
            long id = executeInsertGetKey(connection,
                    "INSERT INTO Parentesco(nombre) VALUES(?)", entity.getNombre());
            entity.setIdParentesco((int) id);
        }
        return entity;
    }

    @Override
    protected Parentesco updateRow(Connection connection, Parentesco entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE Parentesco SET nombre=? WHERE idParentesco=?",
                entity.getNombre(), entity.getIdParentesco());
        return entity;
    }

    @Override
    protected Parentesco mapRow(ResultSet rs) throws SQLException {
        return Parentesco.builder()
                .idParentesco(rs.getInt("idParentesco"))
                .nombre(rs.getString("nombre"))
                .build();
    }
}
