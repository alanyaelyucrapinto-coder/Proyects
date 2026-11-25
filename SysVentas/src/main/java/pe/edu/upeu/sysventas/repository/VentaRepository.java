package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VentaRepository extends AbstractJpaRepository<Venta, String> {

    @Override
    protected String getTableName() {
        return "Venta";
    }

    @Override
    protected String getPkColumn() {
        return "idVenta";
    }

    @Override
    protected Venta mapRow(ResultSet rs) throws SQLException {
        Cliente.ClienteBuilder cliBuilder = Cliente.builder().idDNI(rs.getString("dniCliente"));
        try {
            cliBuilder.nombre(rs.getString("cli_nombre"));
            cliBuilder.direccion(rs.getString("cli_direccion"));
            cliBuilder.celular(rs.getString("cli_celular"));
        } catch (SQLException ignored) {
        }

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getString("idUsuario"));

        Date fecha = rs.getDate("fechaVenta");

        return Venta.builder()
                .idVenta(rs.getString("idVenta"))
                .dniCliente(cliBuilder.build())
                .fechaVenta(fecha != null ? fecha.toLocalDate() : null)
                .netoTotal(rs.getDouble("netoTotal"))
                .igv(rs.getDouble("igv"))
                .precioTotal(rs.getDouble("precioTotal"))
                .idUsuario(usuario)
                .build();
    }

    @Override
    protected Venta insert(Connection conn, Venta e) throws SQLException {
        if (e.getIdVenta() == null || e.getIdVenta().isBlank()) {
            e.setIdVenta(nextCodigo(conn, "Venta", "idVenta", "V", 5));
        }
        executeUpdate(conn,
                "INSERT INTO Venta(idVenta, dniCliente, fechaVenta, netoTotal, igv, precioTotal, idUsuario) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)",
                e.getIdVenta(),
                e.getDniCliente().getIdDNI(),
                Date.valueOf(e.getFechaVenta()),
                e.getNetoTotal(),
                e.getIgv(),
                e.getPrecioTotal(),
                e.getIdUsuario().getIdUsuario());
        return e;
    }

    @Override
    protected Venta updateRow(Connection conn, Venta e) throws SQLException {
        executeUpdate(conn,
                "UPDATE Venta SET dniCliente=?, fechaVenta=?, netoTotal=?, igv=?, precioTotal=?, idUsuario=? " +
                "WHERE idVenta=?",
                e.getDniCliente().getIdDNI(),
                Date.valueOf(e.getFechaVenta()),
                e.getNetoTotal(),
                e.getIgv(),
                e.getPrecioTotal(),
                e.getIdUsuario().getIdUsuario(),
                e.getIdVenta());
        return e;
    }

    @Override
    public Optional<Venta> findById(String idVenta) {
        Optional<Venta> opt = executeQueryOne(
                "SELECT v.*, c.nombre AS cli_nombre, c.direccion AS cli_direccion, c.celular AS cli_celular " +
                "FROM Venta v JOIN Cliente c ON v.dniCliente = c.idDNI WHERE v.idVenta = ?",
                idVenta);
        opt.ifPresent(v -> v.setDetalleVenta(loadDetalles(idVenta)));
        return opt;
    }

    private List<VentaDetalla> loadDetalles(String idVenta) {
        List<VentaDetalla> detalles = new ArrayList<>();
        String sql = "SELECT vd.*, d.nombreFallecido AS def_nombre, d.lugarDefuncion AS def_lugar " +
                     "FROM VentaDetalla vd " +
                     "JOIN Defunciones d ON vd.idDefuncion = d.id WHERE vd.idVenta = ?";
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long idArt = rs.getLong("idArticulo");
                    detalles.add(VentaDetalla.builder()
                            .idVentaDetal(rs.getString("idVentaDetal"))
                            .idArticulo(rs.wasNull() ? null : idArt)
                            .descripcion(rs.getString("descripcion"))
                            .precioUnit(rs.getDouble("precioUnit"))
                            .porceUtil(rs.getDouble("porceUtil"))
                            .cantidad(rs.getInt("cantidad"))
                            .netoTotal(rs.getDouble("netoTotal"))
                            .idDefuncion(Defunciones.builder()
                                    .id(rs.getLong("idDefuncion"))
                                    .nombreFallecido(rs.getString("def_nombre"))
                                    .lugarDefuncion(rs.getString("def_lugar"))
                                    .build())
                            .build());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error cargando detalles de venta", e);
        }
        return detalles;
    }
}
