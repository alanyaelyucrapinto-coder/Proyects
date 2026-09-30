package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Migrante;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio en memoria para las operaciones CRUD del ejercicio.
 */
public class MigranteRepository {

    private final List<Migrante> datos = new ArrayList<>();
    private long secuencia = 1;

    public Migrante guardar(Migrante m) {
        if (m.getId() == null) {
            m.setId(secuencia++);
        }
        datos.add(m);
        return m;
    }

    public Migrante actualizar(Migrante m) {
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(m.getId())) {
                datos.set(i, m);
                return m;
            }
        }
        throw new IllegalArgumentException("No se encontró el registro seleccionado.");
    }

    public List<Migrante> listar() {
        return new ArrayList<>(datos);
    }

    public Optional<Migrante> buscar(Long id) {
        return datos.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    public void eliminar(Long id) {
        datos.removeIf(m -> m.getId().equals(id));
    }
}
