package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Defunciones;
import pe.edu.upeu.sysventas.model.Venta;
import pe.edu.upeu.sysventas.model.VentaDetalla;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VentaDetallaRepository extends AbstractJpaRepository<VentaDetalla, String> {

    @Override
    protected String getTableName() {
        return "VentaDetalla";
    }

    @Override
    protected String getPkColumn() {
        return "idVentaDetal";
    }

    @Override
    protected VentaDetalla mapRow(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setIdVenta(rs.getString("idVenta"));
        Defunciones d = new Defunciones();
        d.setId(rs.getLong("idDefuncion"));
        long idArt = rs.getLong("idArticulo");
        return VentaDetalla.builder()
                .idVentaDetal(rs.getString("idVentaDetal"))
                .idVenta(v)
                .idDefuncion(d)
                .idArticulo(rs.wasNull() ? null : idArt)
                .descripcion(rs.getString("descripcion"))
                .precioUnit(rs.getDouble("precioUnit"))
                .porceUtil(rs.getDouble("porceUtil"))
                .cantidad(rs.getInt("cantidad"))
                .netoTotal(rs.getDouble("netoTotal"))
                .build();
    }

    @Override
    protected VentaDetalla insert(Connection conn, VentaDetalla e) throws SQLException {
        if (e.getIdVentaDetal() == null || e.getIdVentaDetal().isBlank()) {
            e.setIdVentaDetal(nextCodigo(conn, "VentaDetalla", "idVentaDetal", "VD", 4));
        }
        executeUpdate(conn,
                "INSERT INTO VentaDetalla(idVentaDetal, idVenta, idDefuncion, idArticulo, descripcion, precioUnit, porceUtil, cantidad, netoTotal) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                e.getIdVentaDetal(),
                e.getIdVenta().getIdVenta(),
                e.getIdDefuncion().getId(),
                e.getIdArticulo(),
                e.getDescripcion(),
                e.getPrecioUnit(),
                e.getPorceUtil(),
                e.getCantidad(),
                e.getNetoTotal());
        return e;
    }

    @Override
    protected VentaDetalla updateRow(Connection conn, VentaDetalla e) throws SQLException {
        executeUpdate(conn,
                "UPDATE VentaDetalla SET idVenta=?, idDefuncion=?, idArticulo=?, descripcion=?, precioUnit=?, porceUtil=?, cantidad=?, netoTotal=? " +
                "WHERE idVentaDetal=?",
                e.getIdVenta().getIdVenta(),
                e.getIdDefuncion().getId(),
                e.getIdArticulo(),
                e.getDescripcion(),
                e.getPrecioUnit(),
                e.getPorceUtil(),
                e.getCantidad(),
                e.getNetoTotal(),
                e.getIdVentaDetal());
        return e;
    }
}
