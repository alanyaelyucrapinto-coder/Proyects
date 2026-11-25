package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.CausaMuerte;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CausaMuerteRepository extends AbstractJpaRepository<CausaMuerte, String> {

    @Override
    protected String getTableName() {
        return "CausaMuerte";
    }

    @Override
    protected String getPkColumn() {
        return "idCausa";
    }

    @Override
    protected CausaMuerte insert(Connection connection, CausaMuerte entity) throws SQLException {
        if (entity.getIdCausa() == null || entity.getIdCausa().isBlank()) {
            entity.setIdCausa(nextCodigo(connection, "CausaMuerte", "idCausa", "C", 2));
        }
        executeUpdate(connection,
                "INSERT INTO CausaMuerte(idCausa, nombre) VALUES(?, ?)",
                entity.getIdCausa(), entity.getNombre());
        return entity;
    }

    @Override
    protected CausaMuerte updateRow(Connection connection, CausaMuerte entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE CausaMuerte SET nombre=? WHERE idCausa=?",
                entity.getNombre(), entity.getIdCausa());
        return entity;
    }

    @Override
    protected CausaMuerte mapRow(ResultSet rs) throws SQLException {
        return CausaMuerte.builder()
                .idCausa(rs.getString("idCausa"))
                .nombre(rs.getString("nombre"))
                .build();
    }
}
