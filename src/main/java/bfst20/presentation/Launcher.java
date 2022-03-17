package bfst20.presentation;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;


public class Launcher extends Application {
    public static Window primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        //GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        FXMLLoader MainLoader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Scene sceneMain = new Scene(MainLoader.load());//, gd.getDisplayMode().getWidth() - 100, gd.getDisplayMode().getHeight() - 100);

        primaryStage.setMinHeight(720);
        primaryStage.setMinWidth(1280);
        primaryStage.setResizable(true);
        primaryStage.setTitle("Map");
        primaryStage.setScene(sceneMain);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


	public static void main(String[] args) {
        launch(args);
	}
}