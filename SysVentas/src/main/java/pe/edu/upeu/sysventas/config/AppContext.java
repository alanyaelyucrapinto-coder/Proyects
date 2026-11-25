package pe.edu.upeu.sysventas.config;

import pe.edu.upeu.sysventas.controller.*;
import pe.edu.upeu.sysventas.repository.*;
import pe.edu.upeu.sysventas.service.*;
import pe.edu.upeu.sysventas.service.impl.*;
import pe.edu.upeu.sysventas.utils.ConsultaDNI;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class AppContext {

    private static AppContext instance;

    public static synchronized AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    private final Map<Class<?>, Object> contenedor = new HashMap<>();

    private AppContext() {
        registrarRepositorios();
        registrarServicios();
        registrarControladores();
    }

    // CAPA 1 — REPOSITORIOS
    private void registrarRepositorios() {
        registrar(ParentescoRepository.class, new ParentescoRepository());
        registrar(CausaMuerteRepository.class, new CausaMuerteRepository());
        registrar(DefuncionesRepository.class, new DefuncionesRepository());
        registrar(ArticuloFunerarioRepository.class, new ArticuloFunerarioRepository());
        registrar(PerfilRepository.class, new PerfilRepository());
        registrar(UsuarioRepository.class, new UsuarioRepository());
        registrar(ClienteRepository.class, new ClienteRepository());
        registrar(VentCarritoRepository.class, new VentCarritoRepository());
        registrar(VentaDetallaRepository.class, new VentaDetallaRepository());
        registrar(VentaRepository.class, new VentaRepository());
    }

    // CAPA 2 — SERVICIOS
    private void registrarServicios() {
        registrar(IParentescoService.class, new ParentescoServiceImp(getBean(ParentescoRepository.class)));
        registrar(ICausaMuerteService.class, new CausaMuerteServiceImp(getBean(CausaMuerteRepository.class)));
        registrar(IDefuncionesService.class, new DefuncionesServiceImp(getBean(DefuncionesRepository.class)));
        registrar(IArticuloFunerarioService.class, new ArticuloFunerarioServiceImp(getBean(ArticuloFunerarioRepository.class)));

        registrar(IPerfilService.class, new PerfilServiceImp(getBean(PerfilRepository.class)));
        registrar(IUsuarioService.class, new UsuarioServiceImp(getBean(UsuarioRepository.class)));
        registrar(IMenuMenuItemDao.class, new MenuMenuItemDaoImp());

        registrar(ConsultaDNI.class, new ConsultaDNI());
        DataSource ds = DatabaseConfig.getDataSource();
        registrar(IClienteService.class, new ClienteServiceImp(getBean(ClienteRepository.class)));
        registrar(IVentaCarritoService.class, new VentaCarritoServiceImp(getBean(VentCarritoRepository.class)));
        registrar(IVentaDetallaService.class, new VentaDetallaServiceImp(getBean(VentaDetallaRepository.class)));
        registrar(IVentaService.class, new VentaServiceImp(getBean(VentaRepository.class), ds));
    }

    // CAPA 3 — CONTROLADORES JavaFX
    private void registrarControladores() {
        registrar(MainguiController.class, new MainguiController(getBean(IMenuMenuItemDao.class)));

        registrar(DefuncionesController.class,
                new DefuncionesController(
                        getBean(ICausaMuerteService.class),
                        getBean(IParentescoService.class),
                        getBean(IDefuncionesService.class)));

        registrar(CatalogoController.class,
                new CatalogoController(getBean(IArticuloFunerarioService.class)));

        registrar(UsuarioController.class,
                new UsuarioController(
                        getBean(IUsuarioService.class),
                        getBean(IPerfilService.class)));

        registrar(ClienteController.class,
                new ClienteController(
                        getBean(IClienteService.class),
                        getBean(ConsultaDNI.class)));

        registrar(LoginController.class, new LoginController(getBean(IUsuarioService.class)));

        registrar(RegistroController.class,
                new RegistroController(
                        getBean(IUsuarioService.class),
                        getBean(IPerfilService.class)));

        registrar(VentaController.class,
                new VentaController(
                        getBean(IDefuncionesService.class),
                        getBean(IArticuloFunerarioService.class),
                        getBean(IClienteService.class),
                        getBean(IVentaCarritoService.class),
                        getBean(IUsuarioService.class),
                        getBean(IVentaService.class),
                        getBean(IVentaDetallaService.class),
                        getBean(ConsultaDNI.class)));
    }

    private void registrar(Class<?> tipo, Object bean) {
        contenedor.put(tipo, bean);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> tipo) {
        Object bean = contenedor.get(tipo);
        if (bean == null) {
            bean = contenedor.values().stream()
                    .filter(b -> tipo.isAssignableFrom(b.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Bean no encontrado: " + tipo.getName() +
                                    "\n→ ¿Lo registraste en AppContext?"));
        }
        return (T) bean;
    }
}