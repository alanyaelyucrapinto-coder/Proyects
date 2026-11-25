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
    private Long idCarrito;
    private String dniCliente;
    private Defunciones idDefuncion;
    private Long idArticulo;
    private String nombreFallecido; // Contiene detalle: Artículo (Ataúd/Flores) + Fallecido
    private Double precioUnit;
    private Double porceUtil;
    private Integer cantidad;
    private Double netoTotal;
    private int estado;
    private Usuario idUsuario;
}