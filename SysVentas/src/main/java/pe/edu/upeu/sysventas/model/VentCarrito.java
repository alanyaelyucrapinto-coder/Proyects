package pe.edu.upeu.sysventas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentCarrito {
<<<<<<< HEAD
    private Long idCarrito;
    private String dniCliente;
    private Defunciones idDefuncion;
    private Long idArticulo;
    private String nombreFallecido; // Contiene detalle: Artículo (Ataúd/Flores) + Fallecido
    private Double precioUnit;
    private Double porceUtil;
    private Integer cantidad;
    private Double netoTotal;
=======

    private Long idCarrito;
    private String dniruc;
    private Producto idProducto;
    private String nombreProducto;
    private Double cantidad;
    private Double punitario;
    private Double ptotal;
>>>>>>> ea262d381fc3af93599530def55fe13f027ea75e
    private int estado;
    private Usuario idUsuario;
}