package pe.edu.upeu.sysventas.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloFunerario {
    private Long idArticulo;

    @NotBlank(message = "El tipo de artículo (ATAUD o FLORES) es obligatorio")
    private String tipo; // "ATAUD" o "FLORES"

    @NotBlank(message = "El nombre o modelo es obligatorio")
    private String nombre;

    @NotBlank(message = "El tamaño o dimensiones son obligatorios")
    private String tamano;

    @NotBlank(message = "El material de fabricación es obligatorio")
    private String material;

    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser mayor a 0")
    private Double precioUnit;

    @NotNull(message = "El porcentaje de utilidad es obligatorio")
    @PositiveOrZero(message = "El porcentaje de utilidad no puede ser negativo")
    private Double porceUtil;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;
}
