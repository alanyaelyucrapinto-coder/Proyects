package pe.edu.upeu.sysventas.service.impl;

import pe.edu.upeu.sysventas.dto.MenuMenuItenTO;
import pe.edu.upeu.sysventas.service.IMenuMenuItemDao;

import java.util.*;

public class MenuMenuItemDaoImp implements IMenuMenuItemDao {
    @Override
    public List<MenuMenuItenTO> listaAccesos(String perfil, Properties idioma) {
        List<MenuMenuItenTO> lista = new ArrayList<>();

        // 0: Salir / Volver al Login
        lista.add(new MenuMenuItenTO("miprincipal", "/view/login.fxml",
                idioma.getProperty("menu.nombre.principal", "Principal"),
                idioma.getProperty("menuitem.nombre.salir", "Cerrar Sesión"),
                "Salir", "C"));

        // 1: Padrón de Defunciones
        lista.add(new MenuMenuItenTO("midefuncion", "/view/main_defunciones.fxml",
                idioma.getProperty("menu.nombre.defuncion", "Defunciones"),
                idioma.getProperty("menuitem.nombre.defuncion", "Padrón de Defunciones"),
                "Padrón de Defunciones", "T"));

        // 2: Panel de Administrador — Catálogo y Precios de Ataúdes y Flores
        lista.add(new MenuMenuItenTO("micatalogo", "/view/main_catalogo.fxml",
                "Panel Administrador",
                "Precios Ataúdes y Flores",
                "Admin. Precios Ataúdes y Flores", "T"));

        // 3: Panel de Administrador — Gestión de Usuarios
        lista.add(new MenuMenuItenTO("miusuario", "/view/main_usuario.fxml",
                "Panel Administrador",
                "Gestión de Usuarios",
                "Gestión de Usuarios", "T"));

        // 4: Panel de Usuario / Ventas — Clientes
        lista.add(new MenuMenuItenTO("micliente", "/view/main_cliente.fxml",
                "Panel Usuario / Ventas",
                "Reg. Cliente",
                "Gestionar Clientes", "T"));

        // 5: Panel de Usuario / Ventas — Venta de Ataúdes y Flores
        lista.add(new MenuMenuItenTO("miventa", "/view/main_venta.fxml",
                "Panel Usuario / Ventas",
                "Venta Ataúdes y Flores",
                "Venta de Ataúdes y Flores", "T"));

        List<MenuMenuItenTO> accesoReal = new ArrayList<>();
        accesoReal.add(lista.get(0));

        switch (perfil) {
            case "Root":
            case "Administrador":
                accesoReal = lista;
                break;
            case "Usuario":
                accesoReal.add(lista.get(1)); // Padrón de Defunciones
                accesoReal.add(lista.get(4)); // Reg. Cliente
                accesoReal.add(lista.get(5)); // Venta Ataúdes y Flores
                break;
            case "Reporte":
                accesoReal.add(lista.get(1)); // Padrón de Defunciones
                accesoReal.add(lista.get(4)); // Reg. Cliente
                break;
            default:
                accesoReal = lista;
                break;
        }
        return accesoReal;
    }

    @Override
    public Map<String, String[]> accesosAutorizados(List<MenuMenuItenTO> accesos) {
        Map<String, String[]> menuConfig = new HashMap<>();
        for (MenuMenuItenTO menu : accesos) {
            menuConfig.put("mi" + menu.getIdNombreObj(),
                    new String[]{menu.getRutaFile(), menu.getNombreTab(), menu.getTipoTab()});
        }
        return menuConfig;
    }

    @Override
    public int[] contarMenuMunuItem(List<MenuMenuItenTO> data) {
        int menui = 0, menuitem = 0;
        String menuN = "";
        for (MenuMenuItenTO mmiItem : data) {
            if (!mmiItem.getMenunombre().equals(menuN)) {
                menuN = mmiItem.getMenunombre();
                menui++;
            }
            if (!mmiItem.getMenuitemnombre().equals("")) {
                menuitem++;
            }
        }
        return new int[]{menui, menuitem};
    }
}
