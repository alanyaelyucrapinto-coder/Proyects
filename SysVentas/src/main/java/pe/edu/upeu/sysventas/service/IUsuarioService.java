package pe.edu.upeu.sysventas.service;

import pe.edu.upeu.sysventas.model.Usuario;

import java.util.Optional;

public interface IUsuarioService extends ICrudGenericoService<Usuario, String> {
    Optional<Usuario> loginUsuario(String usuario, String clave);
    Optional<Usuario> buscarPorNombreUsuario(String usuario);
}
