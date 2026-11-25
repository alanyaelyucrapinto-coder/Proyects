package pe.edu.upeu.sysventas.model;

import lombok.AllArgsConstructor;
<<<<<<< HEAD
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
=======
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Venta extends DocumentoComercial{
    //private Long idVenta;
    //private Double preciobase;
    //private Double igv;
    //private Double preciototal;
    private Cliente dniruc;
    private Usuario idUsuario;
    //private String numDoc;
    private LocalDateTime fechaGener;
    //private String serie;
    //private String tipoDoc;
    private List<VentaDetalle> detalleVenta;

    @Override
    public double calcularTotal() {
        return getPrecioTotal()+getIgv();
>>>>>>> ea262d381fc3af93599530def55fe13f027ea75e
    }
}