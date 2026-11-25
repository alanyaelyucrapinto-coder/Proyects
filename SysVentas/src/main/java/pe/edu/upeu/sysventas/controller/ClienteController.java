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
import pe.edu.upeu.sysventas.dto.PersonaDto;
import pe.edu.upeu.sysventas.model.Cliente;
import pe.edu.upeu.sysventas.service.IClienteService;
import pe.edu.upeu.sysventas.utils.ConsultaDNI;

import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ClienteController {

    private final IClienteService cs;
    private final ConsultaDNI consultaDNI;

    @FXML
    TextField txtIdDNI, txtNombre, txtDireccion, txtCelular, txtFiltroCliente;

    @FXML
    private TableView<Cliente> tableView;
    ObservableList<Cliente> listarClientes;

    @FXML
    Label lbnMsg;

    @FXML
    private AnchorPane miContenedor;

    Stage stage;
    Cliente formulario;
    String idDniEdit = null;
    private Validator validator;
    private final ToltipCustom ttc = new ToltipCustom();

    @FXML
    public void initialize() {
        TableViewHelper<Cliente> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("DNI", new ColumnInfo("idDNI", 100.0));
        columns.put("Nombre Completo", new ColumnInfo("nombre", 220.0));
        columns.put("Dirección", new ColumnInfo("direccion", 220.0));
        columns.put("Celular", new ColumnInfo("celular", 120.0));

        Consumer<Cliente> updateAction = cliente -> {
            editForm(cliente);
            idDniEdit = cliente.getIdDNI();
        };
        Consumer<Cliente> deleteAction = cliente -> {
            cs.delete(cliente.getIdDNI());
            stage = (Stage) miContenedor.getScene().getWindow();
            double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
            Toast.showToast(stage, "Cliente eliminado correctamente!!", 2000, w, h);
            listar();
        };

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);

        if (txtFiltroCliente != null) {
            txtFiltroCliente.textProperty().addListener((obs, oldVal, newVal) -> filtrarClientes(newVal));
        }

        listar();
    }

    public void listar() {
        try {
            tableView.getItems().clear();
            listarClientes = FXCollections.observableArrayList(cs.findAll());
            tableView.getItems().addAll(listarClientes);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void buscarReniec() {
        String dni = txtIdDNI.getText() != null ? txtIdDNI.getText().trim() : "";
        if (dni.length() == 8) {
            PersonaDto p = consultaDNI.consultarDNI(dni);
            if (p != null && p.getNombre() != null && !p.getNombre().isBlank()) {
                txtNombre.setText((p.getNombre() + " " + p.getApellidoPaterno() + " " + p.getApellidoMaterno()).trim());
            }
        }
    }

    private void filtrarClientes(String filtro) {
        if (listarClientes == null) return;
        if (filtro == null || filtro.isBlank()) {
            tableView.setItems(FXCollections.observableArrayList(listarClientes));
            return;
        }
        String f = filtro.toLowerCase();
        List<Cliente> filtrados = listarClientes.stream()
                .filter(c -> (c.getIdDNI() != null && c.getIdDNI().toLowerCase().contains(f))
                        || (c.getNombre() != null && c.getNombre().toLowerCase().contains(f))
                        || (c.getDireccion() != null && c.getDireccion().toLowerCase().contains(f)))
                .toList();
        tableView.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    public void validarFormulario() {
        formulario = Cliente.builder()
                .idDNI(txtIdDNI.getText() != null ? txtIdDNI.getText().trim() : "")
                .nombre(txtNombre.getText() != null ? txtNombre.getText().trim() : "")
                .direccion(txtDireccion.getText() != null ? txtDireccion.getText().trim() : "")
                .celular(txtCelular.getText() != null ? txtCelular.getText().trim() : "")
                .build();

        Set<ConstraintViolation<Cliente>> violaciones = validator.validate(formulario);
        List<ConstraintViolation<Cliente>> violacionesOrdenadas = violaciones.stream()
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
        txtIdDNI.clear();
        txtIdDNI.setDisable(false);
        txtNombre.clear();
        txtDireccion.clear();
        txtCelular.clear();
        idDniEdit = null;
        limpiarError();
    }

    public void editForm(Cliente c) {
        txtIdDNI.setText(c.getIdDNI());
        txtIdDNI.setDisable(true);
        txtNombre.setText(c.getNombre());
        txtDireccion.setText(c.getDireccion());
        txtCelular.setText(c.getCelular());
        idDniEdit = c.getIdDNI();
        limpiarError();
    }

    private void procesarFormulario() {
        stage = (Stage) miContenedor.getScene().getWindow();
        lbnMsg.setText("Formulario válido");
        lbnMsg.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
        limpiarError();
        double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
        if (idDniEdit != null && !idDniEdit.isBlank()) {
            cs.update(idDniEdit, formulario);
            Toast.showToast(stage, "Cliente actualizado correctamente!!", 2000, w, h);
        } else {
            cs.save(formulario);
            Toast.showToast(stage, "Cliente guardado correctamente!!", 2000, w, h);
        }
        clearForm();
        listar();
    }

    public void limpiarError() {
        List.of(txtIdDNI, txtNombre, txtDireccion, txtCelular)
                .forEach(c -> {
                    c.getStyleClass().remove("text-field-error");
                    ttc.limpiarCampo(c);
                });
    }

    private void mostrarErroresValidacion(List<ConstraintViolation<Cliente>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("idDNI", txtIdDNI);
        campos.put("nombre", txtNombre);
        campos.put("direccion", txtDireccion);
        campos.put("celular", txtCelular);

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
            lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            if (primerCtrl[0] != null) Platform.runLater(primerCtrl[0]::requestFocus);
        }
    }
}
