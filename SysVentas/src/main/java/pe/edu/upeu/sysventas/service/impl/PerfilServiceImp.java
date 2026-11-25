package pe.edu.upeu.sysventas.service.impl;

import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.Perfil;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.repository.PerfilRepository;
import pe.edu.upeu.sysventas.service.IPerfilService;

import java.util.ArrayList;
import java.util.List;

public class PerfilServiceImp extends CrudGenericoServiceImp<Perfil, String> implements IPerfilService {

    private final PerfilRepository repo;

    public PerfilServiceImp(PerfilRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ICrudGenericoRepository<Perfil, String> getRepo() {
        return repo;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> list = new ArrayList<>();
        for (Perfil p : repo.findAll()) {
            list.add(new ComboBoxOption(p.getIdPerfil(), p.getNombre()));
        }
        return list;
    }
}
