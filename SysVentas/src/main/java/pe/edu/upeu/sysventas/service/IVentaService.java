package pe.edu.upeu.sysventas.service;

import pe.edu.upeu.sysventas.dto.comprobante.Comprobante;
import pe.edu.upeu.sysventas.model.Venta;

import java.io.File;

public interface IVentaService extends ICrudGenericoService<Venta, String> {
    File getFile(String filex);
    Comprobante generarComprobante(Venta venta);
}
