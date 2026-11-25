package pe.edu.upeu.sysventas.model;

import lombok.AllArgsConstructor;
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

    public abstract double calcularTotal();
}
