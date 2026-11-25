package pe.edu.upeu.sysventas.model;

import java.time.LocalDate;

/**
 * Persona en proceso de migración.
 */
public class Migrante {

    private Long id;
    private String nombreCompleto;
    private String paisOrigen;
    private String tipoVisa;
    private LocalDate fechaIngreso;
    private EstatusMigratorio estatus;

    public Migrante() {
    }

    public Migrante(Long id, String nombreCompleto, String paisOrigen, String tipoVisa,
                    LocalDate fechaIngreso, EstatusMigratorio estatus) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.paisOrigen = paisOrigen;
        this.tipoVisa = tipoVisa;
        this.fechaIngreso = fechaIngreso;
        this.estatus = estatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    public String getTipoVisa() {
        return tipoVisa;
    }

    public void setTipoVisa(String tipoVisa) {
        this.tipoVisa = tipoVisa;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public EstatusMigratorio getEstatus() {
        return estatus;
    }

    public void setEstatus(EstatusMigratorio estatus) {
        this.estatus = estatus;
    }
}
