package pe.edu.upeu.sysventas.controller;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import pe.edu.upeu.sysventas.model.EstatusMigratorio;
import pe.edu.upeu.sysventas.model.Migrante;
import pe.edu.upeu.sysventas.repository.MigranteRepository;
import pe.edu.upeu.sysventas.service.MigranteService;

import java.time.LocalDate;

public class MigranteController {

    private final MigranteService service = new MigranteService(new MigranteRepository());

    @FXML
    private TextField txtNombre, txtPais, txtBuscar;
    @FXML
    private ComboBox<String> cmbVisa;
    @FXML
    private DatePicker dpIngreso;
    @FXML
    private ToggleGroup grupoEstatus;
    @FXML
    private RadioButton rbRegular, rbIrregular, rbAsilo;
    @FXML
    private TableView<Migrante> tabla;
    @FXML
    private TableColumn<Migrante, Number> colId;
    @FXML
    private TableColumn<Migrante, String> colNombre, colPais, colVisa, colEstatus;
    @FXML
    private TableColumn<Migrante, LocalDate> colIngreso;
    @FXML
    private Label lblEstado;

    private Long idSeleccionado = null;

    @FXML
    public void initialize() {
        rbRegular.setUserData(EstatusMigratorio.REGULAR);
        rbIrregular.setUserData(EstatusMigratorio.IRREGULAR);
        rbAsilo.setUserData(EstatusMigratorio.SOLICITANTE_ASILO);

        cmbVisa.setItems(FXCollections.observableArrayList(
                "Turismo", "Trabajo", "Estudiante", "Residencia", "Humanitaria", "Sin visa"));
        cmbVisa.setEditable(true);

        colId.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getId()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colPais.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaisOrigen()));
        colVisa.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoVisa()));
        colIngreso.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFechaIngreso()));
        colEstatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstatus().getEtiqueta()));

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, m) -> {
            if (m != null) {
                cargarFormulario(m);
            }
        });
        listar();
    }

    @FXML
    public void guardar() {
        try {
            service.guardar(leerFormulario());
            listar();
            limpiar();
            estado("Registro guardado correctamente.");
        } catch (Exception e) {
            alerta(e.getMessage());
        }
    }

    @FXML
    public void actualizar() {
        if (idSeleccionado == null) {
            alerta("Selecciona un registro de la tabla para modificarlo.");
            return;
        }
        try {
            Migrante m = leerFormulario();
            m.setId(idSeleccionado);
            service.actualizar(m);
            listar();
            limpiar();
            estado("Registro actualizado correctamente.");
        } catch (Exception e) {
            alerta(e.getMessage());
        }
    }

    @FXML
    public void eliminar() {
        Migrante m = tabla.getSelectionModel().getSelectedItem();
        if (m == null) {
            alerta("Selecciona un registro para eliminar.");
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar el registro seleccionado?",
                ButtonType.YES, ButtonType.NO);
        a.setHeaderText("Confirmar eliminación");
        if (a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            service.eliminar(m.getId());
            listar();
            limpiar();
            estado("Registro eliminado.");
        }
    }

    @FXML
    public void buscar() {
        String q = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        tabla.setItems(FXCollections.observableArrayList(
                service.listar().stream()
                        .filter(m -> m.getNombreCompleto().toLowerCase().contains(q)
                                || m.getPaisOrigen().toLowerCase().contains(q)
                                || m.getEstatus().getEtiqueta().toLowerCase().contains(q)
                                || String.valueOf(m.getId()).equals(q))
                        .toList()));
    }

    @FXML
    public void listar() {
        tabla.setItems(FXCollections.observableArrayList(service.listar()));
    }

    @FXML
    public void limpiar() {
        idSeleccionado = null;
        txtNombre.clear();
        txtPais.clear();
        cmbVisa.setValue(null);
        dpIngreso.setValue(null);
        grupoEstatus.selectToggle(null);
        tabla.getSelectionModel().clearSelection();
        estado("Formulario listo para un nuevo registro.");
    }

    private Migrante leerFormulario() {
        Migrante m = new Migrante();
        m.setNombreCompleto(txtNombre.getText());
        m.setPaisOrigen(txtPais.getText());
        m.setTipoVisa(cmbVisa.getValue());
        m.setFechaIngreso(dpIngreso.getValue());
        Toggle seleccionado = grupoEstatus.getSelectedToggle();
        m.setEstatus(seleccionado == null ? null : (EstatusMigratorio) seleccionado.getUserData());
        return m;
    }

    private void cargarFormulario(Migrante m) {
        idSeleccionado = m.getId();
        txtNombre.setText(m.getNombreCompleto());
        txtPais.setText(m.getPaisOrigen());
        cmbVisa.setValue(m.getTipoVisa());
        dpIngreso.setValue(m.getFechaIngreso());
        switch (m.getEstatus()) {
            case REGULAR -> rbRegular.setSelected(true);
            case IRREGULAR -> rbIrregular.setSelected(true);
            case SOLICITANTE_ASILO -> rbAsilo.setSelected(true);
        }
        estado("Editando registro N.º " + m.getId());
    }

    private void alerta(String mensaje) {
        new Alert(Alert.AlertType.WARNING, "No se pudo completar la operación.\n" + mensaje, ButtonType.OK)
                .showAndWait();
    }

    private void estado(String mensaje) {
        lblEstado.setText(mensaje);
    }
}
