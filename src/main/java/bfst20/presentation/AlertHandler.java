package bfst20.presentation;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertHandler {
    public static void alertOK(Alert.AlertType type, String text, boolean wait) {
        Alert alert = new Alert(type, text, ButtonType.OK);

        if(wait){
            alert.showAndWait();
        }else{
            alert.show();
        }
    }
}
