package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteRepository extends AbstractJpaRepository<Cliente, String> {

    @Override
    protected String getTableName() {
        return "Cliente";
    }

    @Override
    protected String getPkColumn() {
        return "idDNI";
    }

    @Override
    protected Cliente mapRow(ResultSet rs) throws SQLException {
        return Cliente.builder()
                .idDNI(rs.getString("idDNI"))
                .nombre(rs.getString("nombre"))
                .direccion(rs.getString("direccion"))
                .celular(rs.getString("celular"))
                .build();
    }

    @Override
    protected Cliente insert(Connection conn, Cliente e) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Cliente(idDNI, nombre, direccion, celular) VALUES(?, ?, ?, ?)")) {
            ps.setString(1, e.getIdDNI());
            ps.setString(2, e.getNombre());
            ps.setString(3, e.getDireccion());
            ps.setString(4, e.getCelular());
            ps.executeUpdate();
        }
        return e;
    }

    @Override
    protected Cliente updateRow(Connection conn, Cliente e) throws SQLException {
        executeUpdate(conn,
                "UPDATE Cliente SET nombre=?, direccion=?, celular=? WHERE idDNI=?",
                e.getNombre(),
                e.getDireccion(),
                e.getCelular(),
                e.getIdDNI());
        return e;
    }
}
