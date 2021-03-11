package controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MessageAlert {
    static void showMessage(Stage owner, Alert.AlertType type, String header, String text){
        Alert message=new Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
        message.initOwner(owner);
        message.showAndWait();
    }

    static void showErrorMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Mesaj eroare");
        message.setWidth(400);
        message.setResizable(true);
        message.setContentText(text);
        message.showAndWait();
        message.setWidth(400);
    }

    static void showMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.CONFIRMATION);
        message.initOwner(owner);
        message.setTitle("Mesaj");

        message.setContentText(text);
        message.showAndWait();

    }
}

