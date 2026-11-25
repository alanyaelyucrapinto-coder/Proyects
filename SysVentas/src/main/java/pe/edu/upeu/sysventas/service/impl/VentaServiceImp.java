package pe.edu.upeu.sysventas.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.upeu.sysventas.dto.comprobante.Comprobante;
import pe.edu.upeu.sysventas.dto.comprobante.ItemComprobante;
import pe.edu.upeu.sysventas.model.Venta;
import pe.edu.upeu.sysventas.model.VentaDetalla;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.repository.VentaRepository;
import pe.edu.upeu.sysventas.service.IVentaService;

import javax.sql.DataSource;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class VentaServiceImp extends CrudGenericoServiceImp<Venta, String>
        implements IVentaService {

    private static final Logger logger = LoggerFactory.getLogger(VentaServiceImp.class);

    private final VentaRepository ventaRepository;
    private final DataSource dataSource;

    public VentaServiceImp(VentaRepository ventaRepository, DataSource dataSource) {
        this.ventaRepository = ventaRepository;
        this.dataSource = dataSource;
    }

    @Override
    protected ICrudGenericoRepository<Venta, String> getRepo() {
        return ventaRepository;
    }

    @Override
    public File getFile(String filex) {
        File newFolder = new File("jasper");
        Path camino = Paths.get(newFolder.getAbsolutePath() + "/" + filex);
        logger.debug("Ruta Jasper: {}", camino.toAbsolutePath());
        return camino.toFile();
    }

    @Override
    public Comprobante generarComprobante(Venta venta) {
        Comprobante b = new Comprobante();
        b.setRucEmisor("20154896321");
        b.setRazonSocial("Servicios Funerarios y Padrón de Defunciones S.A.C.");
        b.setDireccion("Jr. Jáuregui 320 - Plaza de Armas");
        b.setUbigeo("Puno, San Román, Juliaca");
        b.setTelefono("951782520");

        b.setTipoDocumento("BOLETA DE VENTA ELECTRÓNICA");
        b.setSerie("B001");
        b.setNumero(venta.getIdVenta() != null ? venta.getIdVenta() : "00001");
        b.setFechaEmision(venta.getFechaVenta() != null ? venta.getFechaVenta().toString() : "");
        b.setMoneda("PEN");

        b.setTipoDocCliente("DNI");
        b.setNroDocCliente(venta.getDniCliente() != null ? venta.getDniCliente().getIdDNI() : "");
        b.setNombreCliente(venta.getDniCliente() != null ? venta.getDniCliente().getNombre() : "");
        String dir = (venta.getDniCliente() != null && venta.getDniCliente().getDireccion() != null
                && !venta.getDniCliente().getDireccion().isBlank())
                ? venta.getDniCliente().getDireccion()
                : "Juliaca - Puno";
        b.setDireccionCliente(dir);

        List<ItemComprobante> items = new ArrayList<>();
        if (venta.getDetalleVenta() != null) {
            for (VentaDetalla vd : venta.getDetalleVenta()) {
                String desc = (vd.getDescripcion() != null && !vd.getDescripcion().isBlank())
                        ? vd.getDescripcion()
                        : ("Servicio Funerario — Fallecido: " +
                           (vd.getIdDefuncion() != null ? vd.getIdDefuncion().getNombreFallecido() : ""));
                items.add(new ItemComprobante(
                        desc,
                        Double.valueOf(vd.getCantidad()),
                        BigDecimal.valueOf(vd.getPrecioUnit())));
            }
        }
        b.setItems(items);

        BigDecimal total = BigDecimal.valueOf(venta.getPrecioTotal()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal neto = BigDecimal.valueOf(venta.getNetoTotal()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal igv = BigDecimal.valueOf(venta.getIgv()).setScale(2, RoundingMode.HALF_UP);

        b.setOpGravadas(neto);
        b.setIgv(igv);
        b.setTotal(total);
        return b;
    }
}
