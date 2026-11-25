package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.ArticuloFunerario;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ArticuloFunerarioRepository extends AbstractJpaRepository<ArticuloFunerario, Long> {

    @Override
    protected String getTableName() {
        return "ArticuloFunerario";
    }

    @Override
    protected String getPkColumn() {
        return "idArticulo";
    }

    @Override
    protected ArticuloFunerario insert(Connection conn, ArticuloFunerario e) throws SQLException {
        long id = executeInsertGetKey(conn,
                "INSERT INTO ArticuloFunerario(tipo, nombre, tamano, material, precioUnit, porceUtil, stock) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)",
                e.getTipo(),
                e.getNombre(),
                e.getTamano(),
                e.getMaterial(),
                e.getPrecioUnit(),
                e.getPorceUtil(),
                e.getStock());
        e.setIdArticulo(id);
        return e;
    }

    @Override
    protected ArticuloFunerario updateRow(Connection conn, ArticuloFunerario e) throws SQLException {
        executeUpdate(conn,
                "UPDATE ArticuloFunerario SET tipo=?, nombre=?, tamano=?, material=?, precioUnit=?, porceUtil=?, stock=? " +
                "WHERE idArticulo=?",
                e.getTipo(),
                e.getNombre(),
                e.getTamano(),
                e.getMaterial(),
                e.getPrecioUnit(),
                e.getPorceUtil(),
                e.getStock(),
                e.getIdArticulo());
        return e;
    }

    @Override
    protected ArticuloFunerario mapRow(ResultSet rs) throws SQLException {
        return ArticuloFunerario.builder()
                .idArticulo(rs.getLong("idArticulo"))
                .tipo(rs.getString("tipo"))
                .nombre(rs.getString("nombre"))
                .tamano(rs.getString("tamano"))
                .material(rs.getString("material"))
                .precioUnit(rs.getDouble("precioUnit"))
                .porceUtil(rs.getDouble("porceUtil"))
                .stock(rs.getInt("stock"))
                .build();
    }

    public List<ArticuloFunerario> findByTipo(String tipo) {
        return executeQuery("SELECT * FROM ArticuloFunerario WHERE UPPER(tipo) = UPPER(?)", tipo);
    }
}
