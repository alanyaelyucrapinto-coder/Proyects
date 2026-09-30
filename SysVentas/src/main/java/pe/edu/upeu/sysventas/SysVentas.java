package pe.edu.upeu.sysventas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
}
