package pe.edu.upeu.sysventas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
<<<<<<< HEAD
    private String idUsuario;
    private String usuario;
    private String clave;
    private Perfil idPerfil;
=======
    private Long idUsuario;
    private String usuario;
    private String clave;
    private Perfil idPerfil;
    private String estado;
>>>>>>> ea262d381fc3af93599530def55fe13f027ea75e
}