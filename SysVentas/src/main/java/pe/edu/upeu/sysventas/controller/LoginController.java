package pe.edu.upeu.sysventas.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.SysVentas;
import pe.edu.upeu.sysventas.components.StageManager;
import pe.edu.upeu.sysventas.config.AppContext;
import pe.edu.upeu.sysventas.dto.SessionManager;
import pe.edu.upeu.sysventas.model.Usuario;
import pe.edu.upeu.sysventas.service.IUsuarioService;

@RequiredArgsConstructor
public class LoginController {

    private final IUsuarioService us;

    @FXML
    TextField txtUsuario;

    @FXML
    PasswordField txtClave;

    @FXML
    Label lblMensaje;

    @FXML
    public void login(ActionEvent e) {
        String user = txtUsuario.getText() != null ? txtUsuario.getText().trim() : "";
        String pass = txtClave.getText() != null ? txtClave.getText().trim() : "";
        if (user.isEmpty() || pass.isEmpty()) {
            mostrarMensaje("Ingrese su usuario y contraseña.", true);
            return;
        }
        us.loginUsuario(user, pass).ifPresentOrElse(
                usuario -> abrirPantalla(e, usuario),
                () -> mostrarMensaje("Usuario o clave incorrectos.", true)
        );
    }

    @FXML
    public void abrirRegistro(ActionEvent e) {
        try {
            AppContext appContext = AppContext.getInstance();
            FXMLLoader loader = new FXMLLoader(SysVentas.class.getResource("/view/registro.fxml"));
            loader.setControllerFactory(appContext::getBean);
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(((Node) e.getSource()).getScene().getWindow());
            dialog.setTitle("Registro de Nuevo Usuario");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.showAndWait();

            RegistroController regCtrl = loader.getController();
            if (regCtrl != null && regCtrl.getUsuarioCreado() != null) {
                txtUsuario.setText(regCtrl.getUsuarioCreado());
                txtClave.clear();
                mostrarMensaje("¡Cuenta creada! Ingrese su clave para iniciar sesión.", false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarMensaje("Error al abrir registro: " + ex.getMessage(), true);
        }
    }

    private void mostrarMensaje(String texto, boolean esError) {
        if (lblMensaje != null) {
            lblMensaje.setText(texto);
            lblMensaje.setStyle(esError
                    ? "-fx-text-fill: #d32f2f; -fx-font-weight: bold;"
                    : "-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        }
        System.out.println(texto);
    }

    @FXML
    public void logout(ActionEvent e) {
        Platform.exit();
        System.exit(0);
    }

    public void abrirPantalla(ActionEvent e, Usuario u) {
        try {
            SessionManager.getInstance().setUserPerfil(u.getIdPerfil().getNombre());
            SessionManager.getInstance().setUserName(u.getUsuario());
            SessionManager.getInstance().setUserId(u.getIdUsuario());

            Screen screen = Screen.getPrimary();
            AppContext appContext = AppContext.getInstance();
            Rectangle2D dimen = screen.getBounds();

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            StageManager.setPrimaryStage(stage);

            FXMLLoader fxmlLoader = new FXMLLoader(SysVentas.class.getResource("/view/maingui.fxml"));
            fxmlLoader.setControllerFactory(appContext::getBean);
            Scene scene = new Scene(fxmlLoader.load(), dimen.getWidth(), dimen.getHeight() - 30);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setTitle("Sistema Funerario y Padrón de Defunciones — Perfil: "
                    + u.getIdPerfil().getNombre() + " (" + u.getUsuario() + ")");
            stage.setX(dimen.getMinX());
            stage.setY(dimen.getMinY());

            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
