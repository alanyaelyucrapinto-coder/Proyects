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
import pe.edu.upeu.sysventas.model.Defunciones;
import pe.edu.upeu.sysventas.service.ICausaMuerteService;
import pe.edu.upeu.sysventas.service.IDefuncionesService;
import pe.edu.upeu.sysventas.service.IParentescoService;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class DefuncionesController {

    private final ICausaMuerteService cms;
    private final IParentescoService prs;
    private final IDefuncionesService dfs;

    @FXML
    ComboBox<ComboBoxOption> cbxCausaMuerte, cbxParentesco;

    @FXML
    TextField txtNombreFallecido, txtLugarDefuncion, txtNombreFamiliar, txtFiltroDato;

    @FXML
    DatePicker dpFechaDefuncion;

    @FXML
    private TableView<Defunciones> tableView;
    ObservableList<Defunciones> listarDefunciones;

    @FXML
    Label lbnMsg;

    @FXML
    private AnchorPane miContenedor;

    Stage stage;
    Defunciones formulario;
    Long idDefuncionCE = 0L;
    private Validator validator;
    private final ToltipCustom ttc = new ToltipCustom();

    @FXML
    public void initialize() {
        cbxCausaMuerte.getItems().addAll(cms.listarCombobox());
        cbxParentesco.getItems().addAll(prs.listarCombobox());
        dpFechaDefuncion.setValue(LocalDate.now());

        TableViewHelper<Defunciones> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("id", 55.0));
        columns.put("Fallecido", new ColumnInfo("nombreFallecido", 180.0));
        columns.put("Fecha Defunción", new ColumnInfo("fechaDefuncion", 110.0));
        columns.put("Lugar Defunción", new ColumnInfo("lugarDefuncion", 180.0));
        columns.put("Causa de Muerte", new ColumnInfo("idCausa.nombre", 160.0));
        columns.put("Familiar Declarante", new ColumnInfo("nombreFamiliar", 160.0));
        columns.put("Parentesco", new ColumnInfo("idParentesco.nombre", 130.0));

        Consumer<Defunciones> updateAction = defuncion -> {
            editForm(defuncion);
            idDefuncionCE = defuncion.getId();
        };
        Consumer<Defunciones> deleteAction = defuncion -> {
            dfs.delete(defuncion.getId());
            stage = (Stage) miContenedor.getScene().getWindow();
            double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
            Toast.showToast(stage, "Se eliminó el registro de defunción correctamente!!", 2000, w, h);
            listar();
        };

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);

        if (txtFiltroDato != null) {
            txtFiltroDato.textProperty().addListener((obs, oldVal, newVal) -> filtrarDefunciones(newVal));
        }

        listar();
    }

    public void listar() {
        try {
            tableView.getItems().clear();
            listarDefunciones = FXCollections.observableArrayList(dfs.findAll());
            tableView.getItems().addAll(listarDefunciones);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void filtrarDefunciones() {
        filtrarDefunciones(txtFiltroDato != null ? txtFiltroDato.getText() : "");
    }

    private void filtrarDefunciones(String filtro) {
        if (listarDefunciones == null) return;
        if (filtro == null || filtro.isBlank()) {
            tableView.setItems(FXCollections.observableArrayList(listarDefunciones));
            return;
        }
        String f = filtro.toLowerCase();
        List<Defunciones> filtrados = listarDefunciones.stream()
                .filter(d -> (d.getNombreFallecido() != null && d.getNombreFallecido().toLowerCase().contains(f))
                        || (d.getLugarDefuncion() != null && d.getLugarDefuncion().toLowerCase().contains(f))
                        || (d.getNombreFamiliar() != null && d.getNombreFamiliar().toLowerCase().contains(f))
                        || (d.getIdCausa() != null && d.getIdCausa().getNombre().toLowerCase().contains(f)))
                .toList();
        tableView.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    public void validarFormulario() {
        formulario = new Defunciones();
        formulario.setNombreFallecido(txtNombreFallecido.getText() != null ? txtNombreFallecido.getText().trim() : "");
        formulario.setFechaDefuncion(dpFechaDefuncion.getValue());
        formulario.setLugarDefuncion(txtLugarDefuncion.getText() != null ? txtLugarDefuncion.getText().trim() : "");
        formulario.setNombreFamiliar(txtNombreFamiliar.getText() != null ? txtNombreFamiliar.getText().trim() : "");

        String idxCausa = cbxCausaMuerte.getSelectionModel().getSelectedItem() == null ? ""
                : cbxCausaMuerte.getSelectionModel().getSelectedItem().getKey();
        formulario.setIdCausa(idxCausa.isEmpty() ? null : cms.findById(idxCausa));

        String idxPar = cbxParentesco.getSelectionModel().getSelectedItem() == null ? "0"
                : cbxParentesco.getSelectionModel().getSelectedItem().getKey();
        formulario.setIdParentesco(idxPar.equals("0") ? null : prs.findById(Integer.parseInt(idxPar)));

        Set<ConstraintViolation<Defunciones>> violaciones = validator.validate(formulario);
        List<ConstraintViolation<Defunciones>> violacionesOrdenadas = violaciones.stream()
                .sorted(Comparator.comparing(v -> v.getPropertyPath().toString())).toList();

        if (violacionesOrdenadas.isEmpty()) {
            procesarFormulario();
            listar();
        } else {
            mostrarErroresValidacion(violacionesOrdenadas);
        }
    }

    @FXML
    public void clearForm() {
        txtNombreFallecido.clear();
        dpFechaDefuncion.setValue(LocalDate.now());
        txtLugarDefuncion.clear();
        cbxCausaMuerte.getSelectionModel().clearSelection();
        txtNombreFamiliar.clear();
        cbxParentesco.getSelectionModel().clearSelection();
        idDefuncionCE = 0L;
        limpiarError();
    }

    public void editForm(Defunciones d) {
        txtNombreFallecido.setText(d.getNombreFallecido());
        dpFechaDefuncion.setValue(d.getFechaDefuncion());
        txtLugarDefuncion.setText(d.getLugarDefuncion());
        txtNombreFamiliar.setText(d.getNombreFamiliar());

        if (d.getIdCausa() != null) {
            cbxCausaMuerte.getSelectionModel().select(
                    cbxCausaMuerte.getItems().stream()
                            .filter(c -> c.getKey().equals(d.getIdCausa().getIdCausa()))
                            .findFirst().orElse(null));
        }

        if (d.getIdParentesco() != null) {
            cbxParentesco.getSelectionModel().select(
                    cbxParentesco.getItems().stream()
                            .filter(p -> Integer.parseInt(p.getKey()) == d.getIdParentesco().getIdParentesco())
                            .findFirst().orElse(null));
        }

        idDefuncionCE = d.getId();
        limpiarError();
    }

    private void procesarFormulario() {
        stage = (Stage) miContenedor.getScene().getWindow();
        lbnMsg.setText("Formulario válido");
        lbnMsg.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
        limpiarError();
        double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
        if (idDefuncionCE > 0L) {
            formulario.setId(idDefuncionCE);
            dfs.update(idDefuncionCE, formulario);
            Toast.showToast(stage, "Se actualizó el registro de defunción correctamente!!", 2000, w, h);
        } else {
            dfs.save(formulario);
            Toast.showToast(stage, "Se guardó el registro de defunción correctamente!!", 2000, w, h);
        }
        clearForm();
        listar();
    }

    public void limpiarError() {
        List.of(txtNombreFallecido, dpFechaDefuncion, txtLugarDefuncion,
                cbxCausaMuerte, txtNombreFamiliar, cbxParentesco)
                .forEach(c -> {
                    c.getStyleClass().remove("text-field-error");
                    ttc.limpiarCampo(c);
                });
    }

    private void mostrarErroresValidacion(List<ConstraintViolation<Defunciones>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombreFallecido", txtNombreFallecido);
        campos.put("fechaDefuncion", dpFechaDefuncion);
        campos.put("lugarDefuncion", txtLugarDefuncion);
        campos.put("idCausa", cbxCausaMuerte);
        campos.put("nombreFamiliar", txtNombreFamiliar);
        campos.put("idParentesco", cbxParentesco);

        LinkedHashMap<String, String> erroresOrdenados = new LinkedHashMap<>();
        final Control[] primerCtrl = {null};
        for (String campo : campos.keySet()) {
            violaciones.stream()
                    .filter(v -> v.getPropertyPath().toString().equals(campo))
                    .findFirst().ifPresent(v -> {
                        erroresOrdenados.put(campo, v.getMessage());
                        Control c = campos.get(campo);
                        if (c != null && !c.getStyleClass().contains("text-field-error")) {
                            ttc.marcarError(c, v.getMessage().trim());
                        }
                        if (primerCtrl[0] == null) primerCtrl[0] = c;
                    });
        }
        if (!erroresOrdenados.isEmpty()) {
            lbnMsg.setText(erroresOrdenados.entrySet().iterator().next().getValue());
            lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            if (primerCtrl[0] != null) Platform.runLater(primerCtrl[0]::requestFocus);
        }
    }
}
