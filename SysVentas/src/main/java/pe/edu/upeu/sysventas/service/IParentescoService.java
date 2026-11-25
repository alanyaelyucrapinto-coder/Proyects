package pe.edu.upeu.sysventas.service;

import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.Parentesco;

import java.util.List;

public interface IParentescoService extends ICrudGenericoService<Parentesco, Integer> {
    List<ComboBoxOption> listarCombobox();
}
