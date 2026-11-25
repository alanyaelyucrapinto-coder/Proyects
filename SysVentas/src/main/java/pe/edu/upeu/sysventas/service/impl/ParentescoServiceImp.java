package pe.edu.upeu.sysventas.service.impl;

import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.Parentesco;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.repository.ParentescoRepository;
import pe.edu.upeu.sysventas.service.IParentescoService;

import java.util.ArrayList;
import java.util.List;

public class ParentescoServiceImp extends CrudGenericoServiceImp<Parentesco, Integer>
        implements IParentescoService {

    private final ParentescoRepository parentescoRepository;

    public ParentescoServiceImp(ParentescoRepository parentescoRepository) {
        this.parentescoRepository = parentescoRepository;
    }

    @Override
    protected ICrudGenericoRepository<Parentesco, Integer> getRepo() {
        return parentescoRepository;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (Parentesco p : parentescoRepository.findAll()) {
            ComboBoxOption cb = new ComboBoxOption();
            cb.setKey(String.valueOf(p.getIdParentesco()));
            cb.setValue(p.getNombre());
            listar.add(cb);
        }
        return listar;
    }
}
