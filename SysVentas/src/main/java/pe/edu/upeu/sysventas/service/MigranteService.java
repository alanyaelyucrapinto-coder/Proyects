package pe.edu.upeu.sysventas.service;

import pe.edu.upeu.sysventas.model.Migrante;
import pe.edu.upeu.sysventas.repository.MigranteRepository;

import java.time.LocalDate;
import java.util.List;

public class MigranteService {

    private final MigranteRepository repository;

    public MigranteService(MigranteRepository repository) {
        this.repository = repository;
    }

    public Migrante guardar(Migrante m) {
        validar(m);
        return repository.guardar(m);
    }

    public Migrante actualizar(Migrante m) {
        validar(m);
        return repository.actualizar(m);
    }

    public List<Migrante> listar() {
        return repository.listar();
    }

    public void eliminar(Long id) {
        repository.eliminar(id);
    }

    private void validar(Migrante m) {
        if (vacio(m.getNombreCompleto())
                || vacio(m.getPaisOrigen())
                || vacio(m.getTipoVisa())
                || m.getFechaIngreso() == null
                || m.getEstatus() == null) {
            throw new IllegalArgumentException("Completa todos los campos y elige un estatus migratorio.");
        }
        if (m.getFechaIngreso().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de ingreso no puede ser futura.");
        }
    }

    private boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }
}
