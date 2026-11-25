package pe.edu.upeu.sysventas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.model.Perfil;
import pe.edu.upeu.sysventas.model.Usuario;
import pe.edu.upeu.sysventas.service.IPerfilService;
import pe.edu.upeu.sysventas.service.IUsuarioService;

/**
 * Registro público desde la pantalla inicial: crea exclusivamente cuentas con perfil "Usuario" (P03).
 * El Panel de Administrador queda oculto y reservado únicamente para quienes inicien sesión
 * con credenciales de Administrador.
 */
@RequiredArgsConstructor
public class RegistroController {

    private final IUsuarioService usuarioService;
    private final IPerfilService perfilService;

    @FXML
    private TextField txtNuevoUsuario;

    @FXML
    private PasswordField txtNuevaClave;

    @FXML
    private PasswordField txtConfirmarClave;

    @FXML
    private Label lblEstado;

    @Getter
    private String usuarioCreado = null;

    @FXML
    public void initialize() {
        usuarioCreado = null;
    }

    @FXML
    public void registrarCuenta(ActionEvent e) {
        String user = txtNuevoUsuario.getText() != null ? txtNuevoUsuario.getText().trim() : "";
        String pass = txtNuevaClave.getText() != null ? txtNuevaClave.getText().trim() : "";
        String confirm = txtConfirmarClave.getText() != null ? txtConfirmarClave.getText().trim() : "";

        if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            lblEstado.setText("Complete todos los campos.");
            lblEstado.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
            return;
        }
        if (user.length() < 3) {
            lblEstado.setText("El usuario debe tener al menos 3 caracteres.");
            lblEstado.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
            return;
        }
        if (!pass.equals(confirm)) {
            lblEstado.setText("Las contraseñas no coinciden.");
            lblEstado.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
            return;
        }
        if (usuarioService.buscarPorNombreUsuario(user).isPresent()) {
            lblEstado.setText("El nombre de usuario ya existe.");
            lblEstado.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
            return;
        }

        // Toda cuenta registrada desde el inicio recibe perfil Usuario ("P03")
        Perfil perfilUsuario = perfilService.findById("P03");

        Usuario nuevo = Usuario.builder()
                .usuario(user)
                .clave(pass)
                .idPerfil(perfilUsuario)
                .build();

        usuarioService.save(nuevo);
        this.usuarioCreado = user;

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancelar(ActionEvent e) {
        this.usuarioCreado = null;
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }
}
