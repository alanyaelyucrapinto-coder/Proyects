package pe.edu.upeu.sysventas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaDetalla {
    private String idVentaDetal;
    private Venta idVenta;
    private Defunciones idDefuncion;
    private Long idArticulo;
    private String descripcion;
    private Double precioUnit;
    private Double porceUtil;
    private Integer cantidad;
    private Double netoTotal;
}
