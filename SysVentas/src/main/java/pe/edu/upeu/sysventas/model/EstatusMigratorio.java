package pe.edu.upeu.sysventas.model;

/**
 * Estatus migratorio de una persona registrada en el padrón.
 */
public enum EstatusMigratorio {

    REGULAR("Regular"),
    IRREGULAR("Irregular"),
    SOLICITANTE_ASILO("Solicitante de asilo");

    private final String etiqueta;

    EstatusMigratorio(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
