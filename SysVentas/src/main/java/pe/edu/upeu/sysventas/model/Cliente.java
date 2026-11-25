package pe.edu.upeu.sysventas.model;

<<<<<<< HEAD
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
=======

>>>>>>> ea262d381fc3af93599530def55fe13f027ea75e
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
=======
import pe.edu.upeu.sysventas.enums.TipoDocumento;
>>>>>>> ea262d381fc3af93599530def55fe13f027ea75e

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
<<<<<<< HEAD
    @NotBlank(message = "El DNI del cliente es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener exactamente 8 dígitos")
    private String idDNI;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombre;

    private String direccion;
    private String celular;
=======
    private String dniruc;
    private String nombres;
    private TipoDocumento tipoDocumento;
    private String repLegal;
    private String direccion;
>>>>>>> ea262d381fc3af93599530def55fe13f027ea75e
}
