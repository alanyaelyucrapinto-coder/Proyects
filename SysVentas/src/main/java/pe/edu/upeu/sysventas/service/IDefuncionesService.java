package pe.edu.upeu.sysventas.service;

import pe.edu.upeu.sysventas.dto.ModeloDataAutocomplet;
import pe.edu.upeu.sysventas.model.Defunciones;

import java.util.List;

public interface IDefuncionesService extends ICrudGenericoService<Defunciones, Long> {
    List<ModeloDataAutocomplet> listAutoCompletDefunciones();
}
