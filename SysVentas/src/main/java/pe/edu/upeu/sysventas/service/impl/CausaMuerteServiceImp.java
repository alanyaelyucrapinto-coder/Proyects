package pe.edu.upeu.sysventas.service.impl;

import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.CausaMuerte;
import pe.edu.upeu.sysventas.repository.CausaMuerteRepository;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.service.ICausaMuerteService;

import java.util.ArrayList;
import java.util.List;

public class CausaMuerteServiceImp extends CrudGenericoServiceImp<CausaMuerte, String>
        implements ICausaMuerteService {

    private final CausaMuerteRepository causaMuerteRepository;

    public CausaMuerteServiceImp(CausaMuerteRepository causaMuerteRepository) {
        this.causaMuerteRepository = causaMuerteRepository;
    }

    @Override
    protected ICrudGenericoRepository<CausaMuerte, String> getRepo() {
        return causaMuerteRepository;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (CausaMuerte cm : causaMuerteRepository.findAll()) {
            ComboBoxOption cb = new ComboBoxOption();
            cb.setKey(cm.getIdCausa());
            cb.setValue(cm.getNombre());
            listar.add(cb);
        }
        return listar;
    }
}
