package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.CausaMuerte;
import pe.edu.upeu.sysventas.model.Defunciones;
import pe.edu.upeu.sysventas.model.Parentesco;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DefuncionesRepository extends AbstractJpaRepository<Defunciones, Long> {

    private static final String SELECT_JOIN =
            "SELECT d.*, cm.nombre AS causa_nombre, p.nombre AS parentesco_nombre " +
            "FROM Defunciones d " +
            "JOIN CausaMuerte cm ON d.idCausa = cm.idCausa " +
            "JOIN Parentesco p ON d.idParentesco = p.idParentesco ";

    @Override
    protected String getTableName() {
        return "Defunciones";
    }

    @Override
    protected String getPkColumn() {
        return "id";
    }

    @Override
    public List<Defunciones> findAll() {
        return executeQuery(SELECT_JOIN);
    }

    @Override
    public Optional<Defunciones> findById(Long id) {
        return executeQueryOne(SELECT_JOIN + "WHERE d.id = ?", id);
    }

    @Override
    protected Defunciones insert(Connection conn, Defunciones e) throws SQLException {
        long id = executeInsertGetKey(conn,
                "INSERT INTO Defunciones(nombreFallecido, fechaDefuncion, lugarDefuncion, idCausa, nombreFamiliar, idParentesco) " +
                "VALUES(?, ?, ?, ?, ?, ?)",
                e.getNombreFallecido(),
                Date.valueOf(e.getFechaDefuncion()),
                e.getLugarDefuncion(),
                e.getIdCausa().getIdCausa(),
                e.getNombreFamiliar(),
                e.getIdParentesco().getIdParentesco());
        e.setId(id);
        return e;
    }

    @Override
    protected Defunciones updateRow(Connection conn, Defunciones e) throws SQLException {
        executeUpdate(conn,
                "UPDATE Defunciones SET nombreFallecido=?, fechaDefuncion=?, lugarDefuncion=?, idCausa=?, nombreFamiliar=?, idParentesco=? " +
                "WHERE id=?",
                e.getNombreFallecido(),
                Date.valueOf(e.getFechaDefuncion()),
                e.getLugarDefuncion(),
                e.getIdCausa().getIdCausa(),
                e.getNombreFamiliar(),
                e.getIdParentesco().getIdParentesco(),
                e.getId());
        return e;
    }

    @Override
    protected Defunciones mapRow(ResultSet rs) throws SQLException {
        Date fecha = rs.getDate("fechaDefuncion");
        return Defunciones.builder()
                .id(rs.getLong("id"))
                .nombreFallecido(rs.getString("nombreFallecido"))
                .fechaDefuncion(fecha != null ? fecha.toLocalDate() : null)
                .lugarDefuncion(rs.getString("lugarDefuncion"))
                .idCausa(CausaMuerte.builder()
                        .idCausa(rs.getString("idCausa"))
                        .nombre(rs.getString("causa_nombre"))
                        .build())
                .nombreFamiliar(rs.getString("nombreFamiliar"))
                .idParentesco(Parentesco.builder()
                        .idParentesco(rs.getInt("idParentesco"))
                        .nombre(rs.getString("parentesco_nombre"))
                        .build())
                .build();
    }
}
