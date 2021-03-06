import Libs.Connector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GUI/gui.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Snake GA");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Connector.stopped = true;
        Connector.started = false;
        if (Connector.phenotypeVisualization) Connector.click_phenotype_visualization();
        super.stop();
    }
}
