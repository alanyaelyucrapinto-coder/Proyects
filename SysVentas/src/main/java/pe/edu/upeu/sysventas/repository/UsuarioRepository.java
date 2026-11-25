package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Perfil;
import pe.edu.upeu.sysventas.model.Usuario;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository extends AbstractJpaRepository<Usuario, String> {

    private static final String SELECT_JOIN =
            "SELECT u.*, p.nombre AS perfil_nombre " +
            "FROM Usuario u JOIN Perfil p ON u.idPerfil = p.idPerfil ";

    @Override
    protected String getTableName() {
        return "Usuario";
    }

    @Override
    protected String getPkColumn() {
        return "idUsuario";
    }

    @Override
    public List<Usuario> findAll() {
        return executeQuery(SELECT_JOIN);
    }

    @Override
    public Optional<Usuario> findById(String id) {
        return executeQueryOne(SELECT_JOIN + "WHERE u.idUsuario = ?", id);
    }

    @Override
    protected Usuario mapRow(ResultSet rs) throws SQLException {
        Perfil perfil = Perfil.builder()
                .idPerfil(rs.getString("idPerfil"))
                .nombre(rs.getString("perfil_nombre"))
                .build();
        return Usuario.builder()
                .idUsuario(rs.getString("idUsuario"))
                .usuario(rs.getString("usuario"))
                .clave(rs.getString("clave"))
                .idPerfil(perfil)
                .build();
    }

    public Optional<Usuario> buscarUsuario(String usuario) {
        return executeQueryOne(SELECT_JOIN + "WHERE u.usuario = ?", usuario);
    }

    public Optional<Usuario> findByUsuarioAndClave(String usuario, String clave) {
        return executeQueryOne(SELECT_JOIN + "WHERE u.usuario = ? AND u.clave = ?",
                usuario, clave);
    }

    @Override
    protected Usuario insert(Connection conn, Usuario entity) throws SQLException {
        if (entity.getIdUsuario() == null || entity.getIdUsuario().isBlank()) {
            entity.setIdUsuario(nextCodigo(conn, "Usuario", "idUsuario", "U", 2));
        }
        executeUpdate(conn,
                "INSERT INTO Usuario(idUsuario, usuario, clave, idPerfil) VALUES(?, ?, ?, ?)",
                entity.getIdUsuario(),
                entity.getUsuario(),
                entity.getClave(),
                entity.getIdPerfil() != null ? entity.getIdPerfil().getIdPerfil() : null);
        return entity;
    }

    @Override
    protected Usuario updateRow(Connection conn, Usuario entity) throws SQLException {
        executeUpdate(conn,
                "UPDATE Usuario SET usuario=?, clave=?, idPerfil=? WHERE idUsuario=?",
                entity.getUsuario(),
                entity.getClave(),
                entity.getIdPerfil() != null ? entity.getIdPerfil().getIdPerfil() : null,
                entity.getIdUsuario());
        return entity;
    }
}
