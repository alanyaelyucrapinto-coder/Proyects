package pe.edu.upeu.sysventas.service;

import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.CausaMuerte;

import java.util.List;

public interface ICausaMuerteService extends ICrudGenericoService<CausaMuerte, String> {
    List<ComboBoxOption> listarCombobox();
}
