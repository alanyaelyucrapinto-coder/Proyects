package pe.edu.upeu.sysventas.controller;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.barcode.QRCode;
import com.github.anastaciocintra.output.PrinterOutputStream;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.sysventas.components.*;
import pe.edu.upeu.sysventas.config.AppContext;
import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.dto.ModeloDataAutocomplet;
import pe.edu.upeu.sysventas.dto.PersonaDto;
import pe.edu.upeu.sysventas.dto.SessionManager;
import pe.edu.upeu.sysventas.dto.comprobante.Comprobante;
import pe.edu.upeu.sysventas.exception.ModelNotFoundException;
import pe.edu.upeu.sysventas.model.*;
import pe.edu.upeu.sysventas.service.*;
import pe.edu.upeu.sysventas.utils.ConsultaDNI;
import pe.edu.upeu.sysventas.utils.PrinterManager;

import javax.print.PrintService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

public class VentaController {
    @FXML
    ComboBox<ComboBoxOption> cbxCategoriaVenta, cbxArticuloFunerario, cbxDefuncion;
    @FXML
    FlowPane contenedorTarjetasCatalogo;
    @FXML
    Button btnAbrirPanelAdmin;
    @FXML
    TextField autocompProducto;
    @FXML
    TextField nombreProducto, txtTamanoSel, txtMaterialSel, codigoPro, stockPro, cantidadPro, punitPro, preTPro, txtBaseImp, txtIgv, txtDescuento, txtImporteT;
    @FXML
    Button btnRegVenta, btnRegCarrito, btnFormCliente, btnRegCliente;
    @FXML
    TextField autocompCliente, txtDireccion, txtCelular;
    @FXML
    TextField razonSocial;
    @FXML
    TextField dniRuc;

    @FXML
    TableView<VentCarrito> tableView;
    AutoCompleteTextField<ModeloDataAutocomplet> actf;
    ModeloDataAutocomplet lastArticulo;

    AutoCompleteTextField<ModeloDataAutocomplet> actfC;
    ModeloDataAutocomplet lastCliente;

    private final IDefuncionesService dfs;
    private final IArticuloFunerarioService artService;
    private final IClienteService cs;
    private final IVentaCarritoService daoC;
    private final IUsuarioService daoU;
    private final IVentaService daoV;
    private final IVentaDetallaService daoVD;
    private final ConsultaDNI cDni;

    Stage stage;
    @FXML
    private AnchorPane miContenedor;

    private final SortedSet<ModeloDataAutocomplet> entries = new TreeSet<>(Comparator.comparing(ModeloDataAutocomplet::toString));
    private final SortedSet<ModeloDataAutocomplet> entriesC = new TreeSet<>(Comparator.comparing(ModeloDataAutocomplet::toString));

    public VentaController(IDefuncionesService dfs, IArticuloFunerarioService artService, IClienteService cs,
                           IVentaCarritoService daoC, IUsuarioService daoU, IVentaService daoV,
                           IVentaDetallaService daoVD, ConsultaDNI cDni) {
        this.dfs = dfs;
        this.artService = artService;
        this.cs = cs;
        this.daoC = daoC;
        this.daoU = daoU;
        this.daoV = daoV;
        this.daoVD = daoVD;
        this.cDni = cDni;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (miContenedor.getScene() != null) {
                stage = (Stage) miContenedor.getScene().getWindow();
            }
        });

        // El botón del Panel de Administrador está OCULTO para usuarios normales;
        // solo es visible cuando inicia sesión el Administrador o Root.
        String perfilActual = SessionManager.getInstance().getUserPerfil();
        boolean esAdmin = "Administrador".equalsIgnoreCase(perfilActual) || "Root".equalsIgnoreCase(perfilActual);
        if (btnAbrirPanelAdmin != null) {
            btnAbrirPanelAdmin.setVisible(esAdmin);
            btnAbrirPanelAdmin.setManaged(esAdmin);
        }

        listarCliente();
        autoCompletarCliente();

        if (cbxCategoriaVenta != null) {
            cbxCategoriaVenta.getItems().clear();
            cbxCategoriaVenta.getItems().add(new ComboBoxOption("TODOS", "Todos (4 Ataúdes y 3 Flores)"));
            cbxCategoriaVenta.getItems().add(new ComboBoxOption("ATAUD", "Solo Ataúdes (4 tipos)"));
            cbxCategoriaVenta.getItems().add(new ComboBoxOption("FLORES", "Solo Flores (3 tipos)"));
            cbxCategoriaVenta.getSelectionModel().selectFirst();
            cbxCategoriaVenta.setOnAction(e -> {
                cargarComboArticulos();
                renderizarTarjetasCatalogo();
            });
        }

        cargarComboArticulos();
        if (cbxArticuloFunerario != null) {
            cbxArticuloFunerario.setOnAction(e -> seleccionarArticuloCombo());
        }

        cargarComboDefunciones();
        renderizarTarjetasCatalogo();

        listarArticulosAutocomplet();
        if (autocompProducto != null) {
            actf = new AutoCompleteTextField<>(entries, autocompProducto);
            autocompProducto.setOnKeyReleased(e -> {
                lastArticulo = actf.getLastSelectedObject();
                if (lastArticulo != null) {
                    aplicarArticuloSeleccionado(Long.parseLong(lastArticulo.getIdx()));
                }
            });
        }

        personalizarTabla();
        btnRegCarrito.setDisable(true);
        btnRegCliente.setDisable(true);
    }

    @FXML
    public void abrirModalPanelAdmin() {
        String perfilActual = SessionManager.getInstance().getUserPerfil();
        boolean esAdmin = "Administrador".equalsIgnoreCase(perfilActual) || "Root".equalsIgnoreCase(perfilActual);
        if (!esAdmin) return;
        try {
            AppContext ctx = AppContext.getInstance();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_catalogo.fxml"));
            loader.setControllerFactory(ctx::getBean);
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            if (miContenedor.getScene() != null) {
                modal.initOwner(miContenedor.getScene().getWindow());
            }
            modal.setTitle("Panel de Administrador — Cambiar Precios, Tamaño y Material / Agregar");
            Scene sc = new Scene(root, 980, 520);
            sc.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            modal.setScene(sc);
            modal.showAndWait();

            // Al cerrar el panel de administrador, refrescar las tarjetas y combos con los nuevos precios/artículos
            cargarComboArticulos();
            listarArticulosAutocomplet();
            renderizarTarjetasCatalogo();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void renderizarTarjetasCatalogo() {
        if (contenedorTarjetasCatalogo == null) return;
        contenedorTarjetasCatalogo.getChildren().clear();

        ComboBoxOption cat = cbxCategoriaVenta != null ? cbxCategoriaVenta.getSelectionModel().getSelectedItem() : null;
        String tipo = cat != null ? cat.getKey() : "TODOS";

        List<ArticuloFunerario> articulos = artService.listarPorTipo(tipo);
        for (ArticuloFunerario art : articulos) {
            VBox card = crearTarjetaCatalogo(art);
            contenedorTarjetasCatalogo.getChildren().add(card);
        }
    }

    private VBox crearTarjetaCatalogo(ArticuloFunerario art) {
        boolean esAtaud = "ATAUD".equalsIgnoreCase(art.getTipo());
        String colorBadge = esAtaud ? "#4a2c11" : "#1e7e34";
        String textoBadge = esAtaud ? "⚱️ ATAÚD" : "💐 FLORES";

        VBox card = new VBox(6);
        card.setPrefWidth(255);
        card.setMinWidth(245);
        card.setMaxWidth(265);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: white; " +
                      "-fx-border-color: #d6dce5; " +
                      "-fx-border-width: 1.5; " +
                      "-fx-border-radius: 10; " +
                      "-fx-background-radius: 10; " +
                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 8, 0, 0, 3);");

        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label badge = new Label(textoBadge);
        badge.setStyle("-fx-background-color: " + colorBadge + "; -fx-text-fill: white; " +
                       "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 12;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label lblStock = new Label("Stock: " + art.getStock());
        if (art.getStock() != null && art.getStock() < 15) {
            lblStock.setStyle("-fx-text-fill: #d32f2f; -fx-background-color: #fde8e8; -fx-padding: 2 6; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            lblStock.setStyle("-fx-text-fill: #555; -fx-font-size: 11px; -fx-font-weight: bold;");
        }
        topRow.getChildren().addAll(badge, spacer, lblStock);

        Label lblTitulo = new Label(art.getNombre());
        lblTitulo.setWrapText(true);
        lblTitulo.setMinHeight(36);
        lblTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1f2d3d;");

        VBox specsBox = new VBox(4);
        specsBox.setPadding(new Insets(6, 8, 6, 8));
        specsBox.setStyle("-fx-background-color: #f8f9fb; -fx-background-radius: 6; -fx-border-color: #edf0f5; -fx-border-radius: 6;");

        Label lblTamano = new Label("📏 Tamaño: " + art.getTamano());
        lblTamano.setWrapText(true);
        lblTamano.setStyle("-fx-font-size: 11.5px; -fx-text-fill: #2c3e50;");

        Label lblMaterial = new Label("🪵 Material: " + art.getMaterial());
        lblMaterial.setWrapText(true);
        lblMaterial.setMinHeight(34);
        lblMaterial.setStyle("-fx-font-size: 11.5px; -fx-text-fill: #34495e; -fx-font-weight: bold;");

        specsBox.getChildren().addAll(lblTamano, lblMaterial);

        HBox bottomRow = new HBox(8);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        bottomRow.setPadding(new Insets(4, 0, 0, 0));

        Label lblPrecio = new Label("S/ " + String.format("%.2f", art.getPrecioUnit()));
        lblPrecio.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #c0392b;");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Button btnComprarCard = new Button("🛒 Seleccionar");
        btnComprarCard.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; " +
                                "-fx-font-weight: bold; -fx-font-size: 11.5px; -fx-padding: 6 10; " +
                                "-fx-background-radius: 6; -fx-cursor: hand;");
        btnComprarCard.setOnAction(ev -> {
            aplicarArticuloSeleccionado(art.getIdArticulo());
            if (dniRuc.getText() != null && !dniRuc.getText().isBlank()) {
                registarPCarrito();
            }
        });

        bottomRow.getChildren().addAll(lblPrecio, spacer2, btnComprarCard);

        card.getChildren().addAll(topRow, lblTitulo, specsBox, bottomRow);
        return card;
    }

    private void cargarComboArticulos() {
        if (cbxArticuloFunerario == null) return;
        ComboBoxOption cat = cbxCategoriaVenta != null ? cbxCategoriaVenta.getSelectionModel().getSelectedItem() : null;
        String tipo = cat != null ? cat.getKey() : "TODOS";
        cbxArticuloFunerario.getItems().clear();
        cbxArticuloFunerario.getItems().addAll(artService.listarPorTipoCombobox(tipo));
    }

    private void cargarComboDefunciones() {
        if (cbxDefuncion == null) return;
        cbxDefuncion.getItems().clear();
        for (Defunciones d : dfs.findAll()) {
            cbxDefuncion.getItems().add(new ComboBoxOption(
                    String.valueOf(d.getId()),
                    d.getId() + " - " + d.getNombreFallecido()));
        }
        if (!cbxDefuncion.getItems().isEmpty()) {
            cbxDefuncion.getSelectionModel().selectFirst();
        }
    }

    private void seleccionarArticuloCombo() {
        ComboBoxOption opt = cbxArticuloFunerario.getSelectionModel().getSelectedItem();
        if (opt != null) {
            aplicarArticuloSeleccionado(Long.parseLong(opt.getKey()));
        }
    }

    private void aplicarArticuloSeleccionado(Long idArt) {
        try {
            ArticuloFunerario art = artService.findById(idArt);
            if (art != null) {
                nombreProducto.setText("[" + art.getTipo() + "] " + art.getNombre());
                if (txtTamanoSel != null) txtTamanoSel.setText(art.getTamano());
                if (txtMaterialSel != null) txtMaterialSel.setText(art.getMaterial());
                codigoPro.setText(String.valueOf(art.getIdArticulo()));
                punitPro.setText(String.valueOf(art.getPrecioUnit()));
                stockPro.setText(String.valueOf(art.getPorceUtil()));
                if (cantidadPro.getText() == null || cantidadPro.getText().isBlank()) {
                    cantidadPro.setText("1");
                }
                calcularPT();
            }
        } catch (Exception ignored) {
        }
    }

    public void listarArticulosAutocomplet() {
        entries.clear();
        entries.addAll(artService.listAutoCompletArticulo());
    }

    public void listarCliente() {
        entriesC.clear();
        entriesC.addAll(cs.listAutoCompletCliente());
    }

    public void autoCompletarCliente() {
        actfC = new AutoCompleteTextField<>(entriesC, autocompCliente);
        autocompCliente.setOnKeyReleased(e -> {
            lastCliente = actfC.getLastSelectedObject();
            if (lastCliente != null) {
                razonSocial.setText(lastCliente.getNameDysplay());
                dniRuc.setText(lastCliente.getIdx());
                if (lastCliente.getOtherData() != null && lastCliente.getOtherData().contains("|")) {
                    String[] partes = lastCliente.getOtherData().split("\\|", -1);
                    txtDireccion.setText(partes[0]);
                    if (txtCelular != null && partes.length > 1) {
                        txtCelular.setText(partes[1]);
                    }
                }
                listar();
            } else {
                btnRegCliente.setDisable(true);
                limpiarFormCliente();
            }
        });
    }

    @FXML
    public void buscarClienteCdni() {
        limpiarFormCliente();
        Stage stage = StageManager.getPrimaryStage();
        double with = stage != null ? stage.getMaxWidth() / 2 : 400;
        String inputDni = autocompCliente.getText() != null ? autocompCliente.getText().trim() : "";
        if (inputDni.length() == 8) {
            try {
                Cliente cli = cs.findById(inputDni);
                if (cli != null) {
                    dniRuc.setText(cli.getIdDNI());
                    razonSocial.setText(cli.getNombre());
                    txtDireccion.setText(cli.getDireccion());
                    if (txtCelular != null) txtCelular.setText(cli.getCelular());
                    btnRegCliente.setDisable(true);
                    if (stage != null) Toast.showToast(stage, "Cliente encontrado en la base de datos", 2000, with, 50);
                    listar();
                    return;
                }
                consultarDNIReniec(with);
            } catch (ModelNotFoundException e) {
                btnRegCliente.setDisable(true);
                if (stage != null) Toast.showToast(stage, "Cliente no registrado, consultando RENIEC...", 2000, with, 50);
                consultarDNIReniec(with);
            }
        } else {
            btnRegCliente.setDisable(true);
            if (stage != null) Toast.showToast(stage, "El DNI buscado debe tener 8 dígitos", 2000, with, 50);
        }
    }

    public void consultarDNIReniec(double with) {
        PersonaDto p = cDni.consultarDNI(autocompCliente.getText().trim());
        if (p != null && p.getDni() != null && !p.getDni().isBlank()) {
            razonSocial.setText((p.getNombre() + " " + p.getApellidoPaterno() + " " + p.getApellidoMaterno()).trim());
            dniRuc.setText(p.getDni());
            btnRegCliente.setDisable(false);
            if (stage != null) Toast.showToast(stage, "Cliente hallado en RENIEC. Haga clic en Add para guardarlo.", 2000, with, 50);
        } else {
            dniRuc.setText(autocompCliente.getText().trim());
            btnRegCliente.setDisable(false);
            if (stage != null) Toast.showToast(stage, "Complete los datos del cliente y presione Add para guardarlo.", 2000, with, 50);
        }
    }

    public void limpiarFormCliente() {
        razonSocial.clear();
        dniRuc.clear();
        txtDireccion.clear();
        if (txtCelular != null) txtCelular.clear();
    }

    @FXML
    public void guardarCliente() {
        Stage stage = StageManager.getPrimaryStage();
        double with = stage != null ? stage.getMaxWidth() / 2 : 400;
        try {
            Cliente c = Cliente.builder()
                    .idDNI(dniRuc.getText().trim())
                    .nombre(razonSocial.getText().trim())
                    .direccion(txtDireccion.getText().trim())
                    .celular(txtCelular != null ? txtCelular.getText().trim() : "")
                    .build();
            cs.save(c);
            btnRegCliente.setDisable(true);
            if (stage != null) Toast.showToast(stage, "¡El cliente se guardó satisfactoriamente!", 2000, with, 50);
            listarCliente();
            listar();
        } catch (Exception e) {
            if (stage != null) Toast.showToast(stage, "Error al guardar cliente: " + e.getMessage(), 2000, with, 50);
        }
    }

    public void personalizarTabla() {
        TableViewHelper<VentCarrito> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID Art.", new ColumnInfo("idArticulo", 60.0));
        columns.put("Ataúd / Flores (Tamaño, Material y Fallecido)", new ColumnInfo("nombreFallecido", 430.0));
        columns.put("Cantidad", new ColumnInfo("cantidad", 75.0));
        columns.put("Precio Unit. (S/)", new ColumnInfo("precioUnit", 110.0));
        columns.put("% Util.", new ColumnInfo("porceUtil", 75.0));
        columns.put("Subtotal (S/)", new ColumnInfo("netoTotal", 110.0));

        Consumer<VentCarrito> updateAction = ventCarrito -> System.out.println("Actualizar: " + ventCarrito);
        Consumer<VentCarrito> deleteAction = this::deleteReg;

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);
    }

    public void listar() {
        tableView.getItems().clear();
        if (dniRuc.getText() == null || dniRuc.getText().isBlank()) return;
        List<VentCarrito> lista = daoC.listaCarritoCliente(dniRuc.getText().trim());
        double impoTotal = 0;
        for (VentCarrito dato : lista) {
            impoTotal += dato.getNetoTotal();
        }
        txtImporteT.setText(String.valueOf(Math.round(impoTotal * 100.0) / 100.0));
        double pv = impoTotal / 1.18;
        txtBaseImp.setText(String.valueOf(Math.round(pv * 100.0) / 100.0));
        txtIgv.setText(String.valueOf(Math.round((impoTotal - pv) * 100.0) / 100.0));
        tableView.getItems().addAll(lista);
    }

    public void deleteReg(VentCarrito obj) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Confirmar acción");
        alert.setContentText("¿Estás seguro de que deseas quitar este artículo del carrito?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            daoC.delete(obj.getIdCarrito());
            Stage stage = StageManager.getPrimaryStage();
            double with = stage != null ? stage.getMaxWidth() / 2 : 400;
            if (stage != null) Toast.showToast(stage, "¡Artículo retirado del carrito!", 2000, with, 50);
            listar();
        }
    }

    @FXML
    private void calcularPT() {
        try {
            if (!cantidadPro.getText().isBlank() && !punitPro.getText().isBlank()) {
                int cant = Integer.parseInt(cantidadPro.getText().trim());
                double pu = Double.parseDouble(punitPro.getText().trim());
                double dato = Math.round(pu * cant * 100.0) / 100.0;
                preTPro.setText(String.valueOf(dato));
                btnRegCarrito.setDisable(cant <= 0 || pu <= 0);
            } else {
                btnRegCarrito.setDisable(true);
            }
        } catch (NumberFormatException ex) {
            btnRegCarrito.setDisable(true);
        }
    }

    private Defunciones obtenerDefuncionSeleccionada() {
        if (cbxDefuncion != null && cbxDefuncion.getSelectionModel().getSelectedItem() != null) {
            Long idDef = Long.parseLong(cbxDefuncion.getSelectionModel().getSelectedItem().getKey());
            return dfs.findById(idDef);
        }
        List<Defunciones> todas = dfs.findAll();
        if (!todas.isEmpty()) {
            return todas.get(0);
        }
        throw new RuntimeException("Registre al menos una defunción en el padrón antes de vender.");
    }


    @FXML
    private void registarPCarrito() {
        Stage stage = StageManager.getPrimaryStage();
        double with = stage != null ? stage.getMaxWidth() / 2 : 400;

        if (dniRuc.getText() == null || dniRuc.getText().isBlank()) {
            if (stage != null) Toast.showToast(stage,
                    "Primero seleccione o busque un Cliente por DNI arriba",
                    2000, with, 50);
            return;
        }

        try {
            Defunciones def = obtenerDefuncionSeleccionada();

            Long idArt = (codigoPro.getText() != null && !codigoPro.getText().isBlank())
                    ? Long.parseLong(codigoPro.getText().trim()) : null;

            int cantidadNueva = Integer.parseInt(cantidadPro.getText().trim());
            double precioUnit = Double.parseDouble(punitPro.getText().trim());

            double porceUtil = (stockPro.getText() == null || stockPro.getText().isBlank())
                    ? 15.0 : Double.parseDouble(stockPro.getText().trim());

            String infoTamMat = "";
            if (txtTamanoSel != null && !txtTamanoSel.getText().isBlank()) {
                infoTamMat = " | Tam: " + txtTamanoSel.getText().trim()
                        + " | Mat: " + txtMaterialSel.getText().trim();
            }

            String descripcionCompleta = nombreProducto.getText().trim()
                    + infoTamMat
                    + " (Fallecido: " + def.getNombreFallecido() + ")";

            // Buscar productos del mismo cliente
            List<VentCarrito> carrito =
                    daoC.listaCarritoCliente(dniRuc.getText().trim());

            VentCarrito existente = null;

            for (VentCarrito item : carrito) {

                boolean mismoArticulo =
                        Objects.equals(item.getIdArticulo(), idArt);

                boolean mismoFallecido =
                        item.getIdDefuncion() != null
                                && Objects.equals(
                                item.getIdDefuncion().getId(),
                                def.getId()
                        );

                if (mismoArticulo && mismoFallecido) {
                    existente = item;
                    break;
                }
            }

            if (existente != null) {

                int nuevaCantidad = existente.getCantidad() + cantidadNueva;
                existente.setCantidad(nuevaCantidad);
                existente.setNetoTotal(nuevaCantidad * existente.getPrecioUnit());

                daoC.update(existente.getIdCarrito(), existente);

                if (stage != null) Toast.showToast(stage,
                        "Cantidad actualizada en el carrito",
                        1500, with, 50);

            } else {

                // Registrar un producto nuevo
                VentCarrito ss = VentCarrito.builder()
                        .dniCliente(dniRuc.getText().trim())
                        .idDefuncion(def)
                        .idArticulo(idArt)
                        .nombreFallecido(descripcionCompleta)
                        .cantidad(cantidadNueva)
                        .precioUnit(precioUnit)
                        .porceUtil(porceUtil)
                        .netoTotal(Double.parseDouble(preTPro.getText().trim()))
                        .estado(1)
                        .idUsuario(daoU.findById(
                                SessionManager.getInstance().getUserId()))
                        .build();

                daoC.save(ss);

                if (stage != null) Toast.showToast(stage,
                        "Artículo agregado al carrito",
                        1500, with, 50);
            }

            listar();

        } catch (Exception e) {
            if (stage != null) Toast.showToast(stage,
                    "Error al agregar al carrito: " + e.getMessage(),
                    2000, with, 50);
        }
    }

    @FXML
    public void registrarVenta() {
        Stage stage = StageManager.getPrimaryStage();
        double with = stage != null ? stage.getMaxWidth() / 2 : 400;
        if (dniRuc.getText() == null || dniRuc.getText().isBlank()) {
            if (stage != null) Toast.showToast(stage, "Seleccione un cliente para registrar la venta", 2000, with, 50);
            return;
        }
        List<VentCarrito> dd = daoC.listaCarritoCliente(dniRuc.getText().trim());
        if (!dd.isEmpty()) {
            Venta to = Venta.builder()
                    .dniCliente(cs.findById(dniRuc.getText().trim()))
                    .netoTotal(Double.parseDouble(txtBaseImp.getText()))
                    .igv(Double.parseDouble(txtIgv.getText()))
                    .precioTotal(Double.parseDouble(txtImporteT.getText()))
                    .idUsuario(daoU.findById(SessionManager.getInstance().getUserId()))
                    .fechaVenta(LocalDate.now())
                    .build();
            Venta idX = daoV.save(to);

            List<VentaDetalla> vdList = new ArrayList<>();
            if (idX.getIdVenta() != null) {
                for (VentCarrito car : dd) {
                    VentaDetalla vd = VentaDetalla.builder()
                            .idVenta(idX)
                            .idDefuncion(dfs.findById(car.getIdDefuncion().getId()))
                            .idArticulo(car.getIdArticulo())
                            .descripcion(car.getNombreFallecido())
                            .cantidad(car.getCantidad())
                            .precioUnit(car.getPrecioUnit())
                            .porceUtil(car.getPorceUtil())
                            .netoTotal(car.getNetoTotal())
                            .build();
                    VentaDetalla vdTemp = daoVD.save(vd);
                    vdList.add(vdTemp);

                    if (car.getIdArticulo() != null) {
                        try {
                            ArticuloFunerario art = artService.findById(car.getIdArticulo());
                            int nuevoStock = Math.max(0, art.getStock() - car.getCantidad());
                            art.setStock(nuevoStock);
                            artService.update(art.getIdArticulo(), art);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            daoC.deleteCarAll(dniRuc.getText().trim());
            listar();
            renderizarTarjetasCatalogo();
            idX.setDetalleVenta(vdList);

            Comprobante comprobante = daoV.generarComprobante(idX);
            try {
                if (comprobante != null) {
                    mostrarVisorComprobante(comprobante);
                    print(idX.getIdVenta());
                } else {
                    if (stage != null) Toast.showToast(stage, "Error al generar comprobante", 2000, with, 50);
                }
            } catch (Exception e) {
                System.out.println("VER: " + e.getMessage());
            }
            resetearVentaDespuesDeEmitir();
        } else {
            if (stage != null) Toast.showToast(stage, "No hay ataúdes ni flores en el carrito", 2000, with, 50);
        }
    }

    private void resetearVentaDespuesDeEmitir() {
        if (autocompCliente != null) autocompCliente.clear();
        lastCliente = null;
        limpiarFormCliente();
        if (btnRegCliente != null) btnRegCliente.setDisable(true);
        if (cbxArticuloFunerario != null) cbxArticuloFunerario.getSelectionModel().clearSelection();
        if (nombreProducto != null) nombreProducto.clear();
        if (txtTamanoSel != null) txtTamanoSel.clear();
        if (txtMaterialSel != null) txtMaterialSel.clear();
        if (codigoPro != null) codigoPro.clear();
        if (stockPro != null) stockPro.clear();
        if (cantidadPro != null) cantidadPro.clear();
        if (punitPro != null) punitPro.clear();
        if (preTPro != null) preTPro.clear();
        if (btnRegCarrito != null) btnRegCarrito.setDisable(true);

        tableView.getItems().clear();
        txtBaseImp.setText("0.0");
        txtIgv.setText("0.0");
        txtDescuento.setText("0.00");
        txtImporteT.setText("0.0");

        Platform.runLater(() -> {
            if (autocompCliente != null) autocompCliente.requestFocus();
        });
    }

    public void mostrarVisorComprobante(Comprobante boleta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_comprobante.fxml"));
            Parent visorRoot = loader.load();
            MainComprobanteController controller = loader.getController();
            controller.pasarDatos(boleta);
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Comprobante de Venta — Ataúdes y Flores");
            alert.setHeaderText(null);
            if (visorRoot instanceof javafx.scene.layout.Region) {
                ((javafx.scene.layout.Region) visorRoot).setPrefSize(800, 600);
            }
            StackPane stackPane = new StackPane(visorRoot);
            alert.getDialogPane().setContent(stackPane);
            ButtonType closeButton = new ButtonType("Cerrar");
            alert.getButtonTypes().add(closeButton);
            alert.setOnCloseRequest(event -> alert.close());
            alert.showAndWait().ifPresent(response -> {
                if (response == closeButton) {
                    alert.close();
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void print(String idv) {
        Venta vt = daoV.findById(idv);
        try {
            PrinterManager printerManager = PrinterManager.getInstance();
            PrintService printService = printerManager.getPrintService();
            PrinterOutputStream printerOutputStream = new PrinterOutputStream(printService);
            EscPos escpos = new EscPos(printerOutputStream);
            Style titleStyle = new Style()
                    .setJustification(EscPosConst.Justification.Center)
                    .setBold(true)
                    .setFontSize(Style.FontSize._2, Style.FontSize._2);
            Style normal = new Style()
                    .setJustification(EscPosConst.Justification.Left_Default);
            Style center = new Style()
                    .setJustification(EscPosConst.Justification.Center);
            escpos.writeLF(titleStyle, "BOLETA FUNERARIA");
            escpos.writeLF(center, "Ataudes y Flores - Juliaca");
            escpos.writeLF(center, "--------------------------------");
            escpos.writeLF(normal, "Cliente: " + vt.getDniCliente().getNombre());
            escpos.writeLF(normal, "DNI: " + vt.getDniCliente().getIdDNI());
            escpos.writeLF(normal, "Fecha: " + vt.getFechaVenta());
            escpos.writeLF(normal, "--------------------------------");
            escpos.writeLF(normal, "Cant Articulo Importe");
            for (VentaDetalla vd : vt.getDetalleVenta()) {
                String desc = vd.getDescripcion() != null ? vd.getDescripcion() : vd.getIdDefuncion().getNombreFallecido();
                String punit = vd.getCantidad() + " " + desc + " S/ " + vd.getNetoTotal();
                escpos.writeLF(normal, punit);
            }
            escpos.writeLF(normal, "--------------------------------");
            escpos.writeLF(normal, "NETO: S/ " + vt.getNetoTotal());
            escpos.writeLF(normal, "IGV: S/ " + vt.getIgv());
            escpos.writeLF(normal, "TOTAL: S/ " + vt.getPrecioTotal());
            escpos.writeLF(normal, "--------------------------------");
            String qrData = "Venta N° " + vt.getIdVenta() + " | Total: S/ " + vt.getPrecioTotal() + " | Fecha: " + vt.getFechaVenta();
            QRCode qrCode = new QRCode()
                    .setJustification(EscPosConst.Justification.Center)
                    .setErrorCorrectionLevel(QRCode.QRErrorCorrectionLevel.QR_ECLEVEL_M_Default)
                    .setModel(QRCode.QRModel._1_Default);
            escpos.write(qrCode, qrData);
            escpos.feed(2);
            escpos.writeLF(center, "Gracias por su compra!");
            escpos.feed(6);
            escpos.cut(EscPos.CutMode.FULL);
            escpos.close();
        } catch (IOException e) {
            System.err.println("Impresora POS no disponible: " + e.getMessage());
        }
    }
}