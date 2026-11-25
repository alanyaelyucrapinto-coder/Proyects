package pe.edu.upeu.sysventas.model;

import lombok.AllArgsConstructor;
<<<<<<< HEAD
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class DocumentoComercial {
    private String idVenta;
    private double netoTotal;
    private double igv;
    private double precioTotal;
=======
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DocumentoComercial {
    private Long id;
    private double precioBase;
    private double igv;
    private double precioTotal;
    private String serie;
    private String numDoc;
    private String tipoDoc;
>>>>>>> ea262d381fc3af93599530def55fe13f027ea75e

    public abstract double calcularTotal();
}
