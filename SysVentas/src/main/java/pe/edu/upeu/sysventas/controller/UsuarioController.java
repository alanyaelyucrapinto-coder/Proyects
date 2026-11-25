package pe.edu.upeu.sysventas.controller;

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
import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.Perfil;
import pe.edu.upeu.sysventas.model.Usuario;
import pe.edu.upeu.sysventas.service.IPerfilService;
import pe.edu.upeu.sysventas.service.IUsuarioService;

import java.util.LinkedHashMap;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;
    private final IPerfilService perfilService;

    @FXML
    private TextField txtUsuarioNombre;

    @FXML
    private PasswordField txtUsuarioClave;

    @FXML
    private ComboBox<ComboBoxOption> cbxPerfil;

    @FXML
    private TableView<Usuario> tableView;
    private ObservableList<Usuario> listaUsuarios;

    @FXML
    private Label lbnMsg;

    @FXML
    private AnchorPane miContenedor;

    private String idUsuarioEdit = null;

    @FXML
    public void initialize() {
        cbxPerfil.getItems().clear();
        cbxPerfil.getItems().addAll(perfilService.listarCombobox());
        cbxPerfil.getSelectionModel().selectFirst();

        TableViewHelper<Usuario> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID Usuario", new ColumnInfo("idUsuario", 90.0));
        columns.put("Usuario", new ColumnInfo("usuario", 180.0));
        columns.put("Clave", new ColumnInfo("clave", 140.0));
        columns.put("Perfil / Rol", new ColumnInfo("idPerfil.nombre", 160.0));

        Consumer<Usuario> updateAction = u -> {
            editForm(u);
            idUsuarioEdit = u.getIdUsuario();
        };
        Consumer<Usuario> deleteAction = u -> {
            usuarioService.delete(u.getIdUsuario());
            Stage stage = (Stage) miContenedor.getScene().getWindow();
            Toast.showToast(stage, "Usuario eliminado correctamente", 2000, stage.getWidth() / 1.5, stage.getHeight() / 2);
            listar();
        };

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);
        listar();
    }

    public void listar() {
        tableView.getItems().clear();
        listaUsuarios = FXCollections.observableArrayList(usuarioService.findAll());
        tableView.getItems().addAll(listaUsuarios);
    }

    @FXML
    public void guardarUsuario() {
        String user = txtUsuarioNombre.getText() != null ? txtUsuarioNombre.getText().trim() : "";
        String pass = txtUsuarioClave.getText() != null ? txtUsuarioClave.getText().trim() : "";
        ComboBoxOption opt = cbxPerfil.getSelectionModel().getSelectedItem();

        if (user.isEmpty() || pass.isEmpty() || opt == null) {
            lbnMsg.setText("Complete usuario, clave y perfil.");
            lbnMsg.setStyle("-fx-text-fill: red;");
            return;
        }

        Perfil p = perfilService.findById(opt.getKey());
        Usuario u = Usuario.builder()
                .idUsuario(idUsuarioEdit)
                .usuario(user)
                .clave(pass)
                .idPerfil(p)
                .build();

        Stage stage = (Stage) miContenedor.getScene().getWindow();
        double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
        if (idUsuarioEdit != null && !idUsuarioEdit.isBlank()) {
            usuarioService.update(idUsuarioEdit, u);
            Toast.showToast(stage, "Usuario actualizado correctamente", 2000, w, h);
        } else {
            usuarioService.save(u);
            Toast.showToast(stage, "Nuevo usuario registrado correctamente", 2000, w, h);
        }
        clearForm();
        listar();
    }

    public void editForm(Usuario u) {
        txtUsuarioNombre.setText(u.getUsuario());
        txtUsuarioClave.setText(u.getClave());
        if (u.getIdPerfil() != null) {
            cbxPerfil.getSelectionModel().select(
                    cbxPerfil.getItems().stream()
                            .filter(o -> o.getKey().equals(u.getIdPerfil().getIdPerfil()))
                            .findFirst().orElse(null));
        }
        idUsuarioEdit = u.getIdUsuario();
    }

    @FXML
    public void clearForm() {
        txtUsuarioNombre.clear();
        txtUsuarioClave.clear();
        cbxPerfil.getSelectionModel().selectFirst();
        idUsuarioEdit = null;
        lbnMsg.setText("");
    }
}
