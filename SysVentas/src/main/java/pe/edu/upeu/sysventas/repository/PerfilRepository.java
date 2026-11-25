package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Perfil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PerfilRepository extends AbstractJpaRepository<Perfil, String> {

    @Override
    protected String getTableName() {
        return "Perfil";
    }

    @Override
    protected String getPkColumn() {
        return "idPerfil";
    }

    @Override
    protected Perfil insert(Connection conn, Perfil entity) throws SQLException {
        if (entity.getIdPerfil() == null || entity.getIdPerfil().isBlank()) {
            entity.setIdPerfil(nextCodigo(conn, "Perfil", "idPerfil", "P", 2));
        }
        executeUpdate(conn,
                "INSERT INTO Perfil(idPerfil, nombre) VALUES(?, ?)",
                entity.getIdPerfil(), entity.getNombre());
        return entity;
    }

    @Override
    protected Perfil updateRow(Connection conn, Perfil entity) throws SQLException {
        executeUpdate(conn,
                "UPDATE Perfil SET nombre=? WHERE idPerfil=?",
                entity.getNombre(), entity.getIdPerfil());
        return entity;
    }

    @Override
    protected Perfil mapRow(ResultSet rs) throws SQLException {
        return Perfil.builder()
                .idPerfil(rs.getString("idPerfil"))
                .nombre(rs.getString("nombre"))
                .build();
    }
}
