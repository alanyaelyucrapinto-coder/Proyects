package pe.edu.upeu.sysventas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Perfil {
<<<<<<< HEAD
    private String idPerfil;
    private String nombre;
=======
    private Long idPerfil;
    private String nombre;
    private String codigo;
>>>>>>> ea262d381fc3af93599530def55fe13f027ea75e
    private List<Acceso> accesos;
}