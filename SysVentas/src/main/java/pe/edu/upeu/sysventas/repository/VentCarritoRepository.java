package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Defunciones;
import pe.edu.upeu.sysventas.model.Usuario;
import pe.edu.upeu.sysventas.model.VentCarrito;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class VentCarritoRepository extends AbstractJpaRepository<VentCarrito, Long> {

    @Override
    protected String getTableName() {
        return "vent_carrito";
    }

    @Override
    protected String getPkColumn() {
        return "id_carrito";
    }

    private static final String SELECT_JOIN =
            "SELECT vc.*, d.nombreFallecido AS def_nombre, d.lugarDefuncion AS def_lugar " +
            "FROM vent_carrito vc " +
            "JOIN Defunciones d ON vc.idDefuncion = d.id ";

    @Override
    public List<VentCarrito> findAll() {
        return executeQuery(SELECT_JOIN);
    }

    @Override
    public Optional<VentCarrito> findById(Long id) {
        return executeQueryOne(SELECT_JOIN + "WHERE vc.id_carrito = ?", id);
    }

    @Override
    protected VentCarrito mapRow(ResultSet rs) throws SQLException {
        VentCarrito vc = new VentCarrito();
        vc.setIdCarrito(rs.getLong("id_carrito"));
        vc.setDniCliente(rs.getString("dniCliente"));
        long idArt = rs.getLong("idArticulo");
        vc.setIdArticulo(rs.wasNull() ? null : idArt);
        vc.setNombreFallecido(rs.getString("nombreFallecido"));
        vc.setPrecioUnit(rs.getDouble("precioUnit"));
        vc.setPorceUtil(rs.getDouble("porceUtil"));
        vc.setCantidad(rs.getInt("cantidad"));
        vc.setNetoTotal(rs.getDouble("netoTotal"));
        vc.setEstado(rs.getInt("estado"));

        Defunciones d = Defunciones.builder()
                .id(rs.getLong("idDefuncion"))
                .nombreFallecido(rs.getString("def_nombre"))
                .lugarDefuncion(rs.getString("def_lugar"))
                .build();
        vc.setIdDefuncion(d);

        Usuario u = new Usuario();
        u.setIdUsuario(rs.getString("idUsuario"));
        vc.setIdUsuario(u);
        return vc;
    }

    @Override
    protected VentCarrito insert(Connection conn, VentCarrito e) throws SQLException {
        long id = executeInsertGetKey(conn,
                "INSERT INTO vent_carrito(dniCliente, idDefuncion, idArticulo, nombreFallecido, precioUnit, porceUtil, cantidad, netoTotal, estado, idUsuario) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                e.getDniCliente(),
                e.getIdDefuncion().getId(),
                e.getIdArticulo(),
                e.getNombreFallecido(),
                e.getPrecioUnit(),
                e.getPorceUtil(),
                e.getCantidad(),
                e.getNetoTotal(),
                e.getEstado(),
                e.getIdUsuario().getIdUsuario());
        e.setIdCarrito(id);
        return e;
    }

    @Override
    protected VentCarrito updateRow(Connection conn, VentCarrito e) throws SQLException {
        executeUpdate(conn,
                "UPDATE vent_carrito SET dniCliente=?, idDefuncion=?, idArticulo=?, nombreFallecido=?, precioUnit=?, porceUtil=?, cantidad=?, netoTotal=?, estado=? " +
                "WHERE id_carrito=?",
                e.getDniCliente(),
                e.getIdDefuncion().getId(),
                e.getIdArticulo(),
                e.getNombreFallecido(),
                e.getPrecioUnit(),
                e.getPorceUtil(),
                e.getCantidad(),
                e.getNetoTotal(),
                e.getEstado(),
                e.getIdCarrito());
        return e;
    }

    public List<VentCarrito> listaCarritoCliente(String dniCliente) {
        return executeQuery(SELECT_JOIN + "WHERE vc.dniCliente = ?", dniCliente);
    }

    public void deleteByDniCliente(String dniCliente) {
        executeUpdateStandalone("DELETE FROM vent_carrito WHERE dniCliente = ?", dniCliente);
    }
}
