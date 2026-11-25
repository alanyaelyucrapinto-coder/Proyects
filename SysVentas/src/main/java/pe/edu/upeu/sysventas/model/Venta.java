package pe.edu.upeu.sysventas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Venta extends DocumentoComercial {
    private Cliente dniCliente;
    private LocalDate fechaVenta;
    private Usuario idUsuario;
    private List<VentaDetalla> detalleVenta;

    @Override
    public double calcularTotal() {
        return getNetoTotal() + getIgv();
    }
}