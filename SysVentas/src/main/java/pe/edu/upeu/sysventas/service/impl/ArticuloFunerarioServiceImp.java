package pe.edu.upeu.sysventas.service.impl;

import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.dto.ModeloDataAutocomplet;
import pe.edu.upeu.sysventas.model.ArticuloFunerario;
import pe.edu.upeu.sysventas.repository.ArticuloFunerarioRepository;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.service.IArticuloFunerarioService;

import java.util.ArrayList;
import java.util.List;

public class ArticuloFunerarioServiceImp extends CrudGenericoServiceImp<ArticuloFunerario, Long>
        implements IArticuloFunerarioService {

    private final ArticuloFunerarioRepository repo;

    public ArticuloFunerarioServiceImp(ArticuloFunerarioRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ICrudGenericoRepository<ArticuloFunerario, Long> getRepo() {
        return repo;
    }

    @Override
    public List<ComboBoxOption> listarTipos() {
        List<ComboBoxOption> list = new ArrayList<>();
        list.add(new ComboBoxOption("ATAUD", "Ataúd"));
        list.add(new ComboBoxOption("FLORES", "Arreglo de Flores"));
        return list;
    }

    @Override
    public List<ArticuloFunerario> listarPorTipo(String tipo) {
        if (tipo == null || tipo.isBlank() || tipo.equalsIgnoreCase("TODOS")) {
            return repo.findAll();
        }
        return repo.findByTipo(tipo);
    }

    @Override
    public List<ComboBoxOption> listarPorTipoCombobox(String tipo) {
        List<ComboBoxOption> list = new ArrayList<>();
        List<ArticuloFunerario> articulos = listarPorTipo(tipo);
        for (ArticuloFunerario a : articulos) {
            list.add(new ComboBoxOption(
                    String.valueOf(a.getIdArticulo()),
                    "[" + a.getTipo() + "] " + a.getNombre() + " | " + a.getMaterial() + " — S/ " + String.format("%.2f", a.getPrecioUnit())));
        }
        return list;
    }

    @Override
    public List<ComboBoxOption> listarTodosCombobox() {
        return listarPorTipoCombobox("TODOS");
    }

    @Override
    public List<ModeloDataAutocomplet> listAutoCompletArticulo() {
        List<ModeloDataAutocomplet> list = new ArrayList<>();
        for (ArticuloFunerario a : repo.findAll()) {
            ModeloDataAutocomplet d = new ModeloDataAutocomplet();
            d.setIdx(String.valueOf(a.getIdArticulo()));
            d.setNameDysplay("[" + a.getTipo() + "] " + a.getNombre() + " (" + a.getMaterial() + ")");
            d.setOtherData(a.getPrecioUnit() + ":" + a.getPorceUtil() + ":" + a.getStock() + ":" + a.getTipo());
            list.add(d);
        }
        return list;
    }
}
