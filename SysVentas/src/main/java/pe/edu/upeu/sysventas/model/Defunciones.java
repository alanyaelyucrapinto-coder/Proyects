package pe.edu.upeu.sysventas.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Defunciones {
    private Long id;

    @NotBlank(message = "El nombre del fallecido es obligatorio")
    private String nombreFallecido;

    @NotNull(message = "La fecha de defunción es obligatoria")
    @PastOrPresent(message = "La fecha de defunción no puede ser futura")
    private LocalDate fechaDefuncion;

    @NotBlank(message = "El lugar de defunción es obligatorio")
    private String lugarDefuncion;

    @NotNull(message = "La causa de muerte es obligatoria")
    private CausaMuerte idCausa;

    @NotBlank(message = "El nombre del familiar declarante es obligatorio")
    private String nombreFamiliar;

    @NotNull(message = "El parentesco del familiar es obligatorio")
    private Parentesco idParentesco;
}
