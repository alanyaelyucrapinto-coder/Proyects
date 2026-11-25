package pe.edu.upeu.sysventas.service.impl;

import pe.edu.upeu.sysventas.model.VentaDetalla;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.repository.VentaDetallaRepository;
import pe.edu.upeu.sysventas.service.IVentaDetallaService;

public class VentaDetallaServiceImp extends CrudGenericoServiceImp<VentaDetalla, String>
        implements IVentaDetallaService {

    private final VentaDetallaRepository repository;

    public VentaDetallaServiceImp(VentaDetallaRepository repository) {
        this.repository = repository;
    }

    @Override
    protected ICrudGenericoRepository<VentaDetalla, String> getRepo() {
        return repository;
    }
}
