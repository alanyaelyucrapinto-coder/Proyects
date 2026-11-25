package pe.edu.upeu.sysventas.repository.helper;

import pe.edu.upeu.sysventas.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SqlHelper Capa de Acceso a Datos (JDBC puro)
 */
public abstract class SqlHelper<T> {

    protected Connection openConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected List<T> executeQuery(String sql, Object... params) {
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en query: " + e.getMessage(), e);
        }
    }

    protected Optional<T> executeQueryOne(String sql, Object... params) {
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en queryOne: " + e.getMessage(), e);
        }
    }

    protected boolean executeExists(String sql, Object id) {
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en existsById: " + e.getMessage(), e);
        }
    }

    protected long executeInsertGetKey(Connection conn, String sql, Object... params)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("No se generó clave para: " + sql);
            }
        }
    }

    protected void executeUpdate(Connection conn, String sql, Object... params)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            ps.executeUpdate();
        }
    }

    protected int executeUpdateStandalone(String sql, Object... params) {
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            setParams(ps, params);
            int rows = ps.executeUpdate();
            conn.commit();
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Error en executeUpdate: " + e.getMessage(), e);
        }
    }

    /**
     * Genera un código secuencial con prefijo (p. ej. "V00001", "VD0001", "C01")
     * para tablas cuya clave primaria es VARCHAR en el modelo SQL.
     */
    protected String nextCodigo(Connection conn, String tableName, String pkCol, String prefix, int digits)
            throws SQLException {
        String sql = "SELECT " + pkCol + " FROM " + tableName;
        int maxNum = 0;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String val = rs.getString(1);
                if (val != null) {
                    String digitsOnly = val.replaceAll("\\D+", "");
                    if (!digitsOnly.isEmpty()) {
                        try {
                            int n = Integer.parseInt(digitsOnly);
                            if (n > maxNum) maxNum = n;
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }
        return String.format("%s%0" + digits + "d", prefix, maxNum + 1);
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}