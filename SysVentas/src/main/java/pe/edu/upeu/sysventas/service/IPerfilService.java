package pe.edu.upeu.sysventas.service;

import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.Perfil;

import java.util.List;

public interface IPerfilService extends ICrudGenericoService<Perfil, String> {
    List<ComboBoxOption> listarCombobox();
}
