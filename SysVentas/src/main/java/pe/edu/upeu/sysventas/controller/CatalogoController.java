package pe.edu.upeu.sysventas.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.components.ColumnInfo;
import pe.edu.upeu.sysventas.components.TableViewHelper;
import pe.edu.upeu.sysventas.components.Toast;
import pe.edu.upeu.sysventas.components.ToltipCustom;
import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.ArticuloFunerario;
import pe.edu.upeu.sysventas.service.IArticuloFunerarioService;

import java.util.*;
import java.util.function.Consumer;

/**
 * Panel de Administrador para gestionar el catálogo de Ataúdes (4 tipos base)
 * y Arreglos de Flores (3 tipos base): cambiar precios, tamaño, material, stock y agregar nuevos.
 */
@RequiredArgsConstructor
public class CatalogoController {

    private final IArticuloFunerarioService articuloService;

    @FXML
    private ComboBox<ComboBoxOption> cbxTipoArticulo;

    @FXML
    private TextField txtNombreArticulo, txtTamano, txtMaterial, txtPrecioUnit, txtPorceUtil, txtStock, txtFiltroCatalogo;

    @FXML
    private TableView<ArticuloFunerario> tableView;
    private ObservableList<ArticuloFunerario> listaArticulos;

    @FXML
    private Label lbnMsg;

    @FXML
    private AnchorPane miContenedor;

    private Stage stage;
    private ArticuloFunerario formulario;
    private Long idArticuloEdit = 0L;
    private Validator validator;
    private final ToltipCustom ttc = new ToltipCustom();

    @FXML
    public void initialize() {
        cbxTipoArticulo.getItems().clear();
        cbxTipoArticulo.getItems().addAll(articuloService.listarTipos());
        cbxTipoArticulo.getSelectionModel().selectFirst();

        TableViewHelper<ArticuloFunerario> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("idArticulo", 50.0));
        columns.put("Categoría", new ColumnInfo("tipo", 90.0));
        columns.put("Modelo (Ataúd / Flores)", new ColumnInfo("nombre", 190.0));
        columns.put("Tamaño / Medidas", new ColumnInfo("tamano", 200.0));
        columns.put("Material de Fabricación", new ColumnInfo("material", 240.0));
        columns.put("Precio (S/)", new ColumnInfo("precioUnit", 95.0));
        columns.put("% Util.", new ColumnInfo("porceUtil", 75.0));
        columns.put("Stock", new ColumnInfo("stock", 70.0));

        Consumer<ArticuloFunerario> updateAction = art -> {
            editForm(art);
            idArticuloEdit = art.getIdArticulo();
        };
        Consumer<ArticuloFunerario> deleteAction = art -> {
            articuloService.delete(art.getIdArticulo());
            stage = (Stage) miContenedor.getScene().getWindow();
            double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
            Toast.showToast(stage, "Artículo eliminado del catálogo correctamente!!", 2000, w, h);
            listar();
        };

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);

        if (txtFiltroCatalogo != null) {
            txtFiltroCatalogo.textProperty().addListener((obs, oldVal, newVal) -> filtrar(newVal));
        }

        listar();
    }

    public void listar() {
        try {
            tableView.getItems().clear();
            listaArticulos = FXCollections.observableArrayList(articuloService.findAll());
            tableView.getItems().addAll(listaArticulos);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void filtrar(String texto) {
        if (listaArticulos == null) return;
        if (texto == null || texto.isBlank()) {
            tableView.setItems(FXCollections.observableArrayList(listaArticulos));
            return;
        }
        String f = texto.toLowerCase();
        List<ArticuloFunerario> filtrados = listaArticulos.stream()
                .filter(a -> (a.getNombre() != null && a.getNombre().toLowerCase().contains(f))
                        || (a.getTipo() != null && a.getTipo().toLowerCase().contains(f))
                        || (a.getMaterial() != null && a.getMaterial().toLowerCase().contains(f))
                        || (a.getTamano() != null && a.getTamano().toLowerCase().contains(f)))
                .toList();
        tableView.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    public void validarFormulario() {
        ComboBoxOption tipoOpt = cbxTipoArticulo.getSelectionModel().getSelectedItem();
        String tipo = tipoOpt != null ? tipoOpt.getKey() : "";

        formulario = ArticuloFunerario.builder()
                .tipo(tipo)
                .nombre(txtNombreArticulo.getText() != null ? txtNombreArticulo.getText().trim() : "")
                .tamano(txtTamano.getText() != null ? txtTamano.getText().trim() : "")
                .material(txtMaterial.getText() != null ? txtMaterial.getText().trim() : "")
                .precioUnit(parseDoubleSafe(txtPrecioUnit.getText()))
                .porceUtil(parseDoubleSafe(txtPorceUtil.getText()))
                .stock(parseIntSafe(txtStock.getText()))
                .build();

        Set<ConstraintViolation<ArticuloFunerario>> violaciones = validator.validate(formulario);
        List<ConstraintViolation<ArticuloFunerario>> violacionesOrdenadas = violaciones.stream()
                .sorted(Comparator.comparing(v -> v.getPropertyPath().toString())).toList();

        if (violacionesOrdenadas.isEmpty()) {
            procesarFormulario();
            listar();
        } else {
            mostrarErroresValidacion(violacionesOrdenadas);
        }
    }

    private double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }

    private int parseIntSafe(String value) {
        if (value == null || value.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @FXML
    public void clearForm() {
        cbxTipoArticulo.getSelectionModel().selectFirst();
        txtNombreArticulo.clear();
        txtTamano.clear();
        txtMaterial.clear();
        txtPrecioUnit.clear();
        txtPorceUtil.clear();
        txtStock.clear();
        idArticuloEdit = 0L;
        limpiarError();
    }

    public void editForm(ArticuloFunerario art) {
        cbxTipoArticulo.getSelectionModel().select(
                cbxTipoArticulo.getItems().stream()
                        .filter(o -> o.getKey().equalsIgnoreCase(art.getTipo()))
                        .findFirst().orElse(null));
        txtNombreArticulo.setText(art.getNombre());
        txtTamano.setText(art.getTamano());
        txtMaterial.setText(art.getMaterial());
        txtPrecioUnit.setText(String.valueOf(art.getPrecioUnit()));
        txtPorceUtil.setText(String.valueOf(art.getPorceUtil()));
        txtStock.setText(String.valueOf(art.getStock()));
        idArticuloEdit = art.getIdArticulo();
        limpiarError();
    }

    private void procesarFormulario() {
        stage = (Stage) miContenedor.getScene().getWindow();
        lbnMsg.setText("Catálogo actualizado correctamente");
        lbnMsg.setStyle("-fx-text-fill: green; -fx-font-size: 15px;");
        limpiarError();
        double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
        if (idArticuloEdit > 0L) {
            formulario.setIdArticulo(idArticuloEdit);
            articuloService.update(idArticuloEdit, formulario);
            Toast.showToast(stage, "¡Precio, Tamaño y Material actualizados!", 2000, w, h);
        } else {
            articuloService.save(formulario);
            Toast.showToast(stage, "¡Nuevo Ataúd / Arreglo Floral agregado correctamente!", 2000, w, h);
        }
        clearForm();
        listar();
    }

    public void limpiarError() {
        List.of(cbxTipoArticulo, txtNombreArticulo, txtTamano, txtMaterial, txtPrecioUnit, txtPorceUtil, txtStock)
                .forEach(c -> {
                    c.getStyleClass().remove("text-field-error");
                    ttc.limpiarCampo(c);
                });
    }

    private void mostrarErroresValidacion(List<ConstraintViolation<ArticuloFunerario>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("tipo", cbxTipoArticulo);
        campos.put("nombre", txtNombreArticulo);
        campos.put("tamano", txtTamano);
        campos.put("material", txtMaterial);
        campos.put("precioUnit", txtPrecioUnit);
        campos.put("porceUtil", txtPorceUtil);
        campos.put("stock", txtStock);

        LinkedHashMap<String, String> erroresOrdenados = new LinkedHashMap<>();
        final Control[] primerCtrl = {null};
        for (String campo : campos.keySet()) {
            violaciones.stream()
                    .filter(v -> v.getPropertyPath().toString().equals(campo))
                    .findFirst().ifPresent(v -> {
                        erroresOrdenados.put(campo, v.getMessage());
                        Control c = campos.get(campo);
                        if (c != null) ttc.marcarError(c, v.getMessage().trim());
                        if (primerCtrl[0] == null) primerCtrl[0] = c;
                    });
        }
        if (!erroresOrdenados.isEmpty()) {
            lbnMsg.setText(erroresOrdenados.entrySet().iterator().next().getValue());
            lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 15px;");
            if (primerCtrl[0] != null) Platform.runLater(primerCtrl[0]::requestFocus);
        }
    }
}
