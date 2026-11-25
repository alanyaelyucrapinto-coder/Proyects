package pe.edu.upeu.sysventas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
<<<<<<< HEAD
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pe.edu.upeu.sysventas.config.AppContext;

import java.io.IOException;

public class SysVentas extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Screen screen=Screen.getPrimary();

        AppContext appContext=AppContext.getInstance();
        Rectangle2D dimen=screen.getBounds();

        FXMLLoader fxmlLoader = new FXMLLoader(SysVentas.class.getResource("/view/login.fxml"));
        fxmlLoader.setControllerFactory(appContext::getBean);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("SysVentas Login!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
=======
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SysVentas extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(SysVentas.class.getResource("/view/padron_migrantes.fxml"));
        Scene scene = new Scene(loader.load(), 1250, 780);
        stage.setTitle("Registro Migratorio - Padrón de Migrantes");
        stage.setMinWidth(1050);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
>>>>>>> ea262d381fc3af93599530def55fe13f027ea75e
}
