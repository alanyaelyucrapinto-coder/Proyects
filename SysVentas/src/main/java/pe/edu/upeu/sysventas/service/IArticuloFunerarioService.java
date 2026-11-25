package pe.edu.upeu.sysventas.service;

import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.dto.ModeloDataAutocomplet;
import pe.edu.upeu.sysventas.model.ArticuloFunerario;

import java.util.List;

public interface IArticuloFunerarioService extends ICrudGenericoService<ArticuloFunerario, Long> {
    List<ComboBoxOption> listarTipos();
    List<ComboBoxOption> listarPorTipoCombobox(String tipo);
    List<ComboBoxOption> listarTodosCombobox();
    List<ArticuloFunerario> listarPorTipo(String tipo);
    List<ModeloDataAutocomplet> listAutoCompletArticulo();
}
