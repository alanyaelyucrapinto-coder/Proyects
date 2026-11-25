package pe.edu.upeu.sysventas.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.upeu.sysventas.dto.ModeloDataAutocomplet;
import pe.edu.upeu.sysventas.model.Defunciones;
import pe.edu.upeu.sysventas.repository.DefuncionesRepository;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.service.IDefuncionesService;

import java.util.ArrayList;
import java.util.List;

public class DefuncionesServiceImp extends CrudGenericoServiceImp<Defunciones, Long>
        implements IDefuncionesService {

    private static final Logger logger = LoggerFactory.getLogger(DefuncionesServiceImp.class);

    private final DefuncionesRepository defuncionesRepository;

    public DefuncionesServiceImp(DefuncionesRepository defuncionesRepository) {
        this.defuncionesRepository = defuncionesRepository;
    }

    @Override
    protected ICrudGenericoRepository<Defunciones, Long> getRepo() {
        return defuncionesRepository;
    }

    @Override
    public List<Defunciones> findAll() {
        return defuncionesRepository.findAll();
    }

    @Override
    public List<ModeloDataAutocomplet> listAutoCompletDefunciones() {
        List<ModeloDataAutocomplet> listar = new ArrayList<>();
        try {
            defuncionesRepository.findAll().forEach(defuncion -> {
                ModeloDataAutocomplet data = new ModeloDataAutocomplet();
                data.setIdx(String.valueOf(defuncion.getId()));
                data.setNameDysplay(defuncion.getNombreFallecido());
                String causa = defuncion.getIdCausa() != null ? defuncion.getIdCausa().getNombre() : "";
                String familiar = defuncion.getNombreFamiliar() != null ? defuncion.getNombreFamiliar() : "";
                data.setOtherData("50.00:10.0:" + causa + ":" + familiar);
                listar.add(data);
            });
        } catch (Exception e) {
            logger.error("Error al realizar la búsqueda de defunciones", e);
        }
        return listar;
    }
}
