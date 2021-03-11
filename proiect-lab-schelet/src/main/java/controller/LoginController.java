package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jdk.tools.jlink.internal.Platform;
import socialnetwork.domain.CerereDePrietenie;
import socialnetwork.domain.MD5;
import socialnetwork.domain.UserPage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.*;
import utils.events.CerereDePrietenieChangeEvent;
import utils.events.PrietenieChangeEvent;
import utils.events.UtilizatorChangeEvent;
import utils.observers.Observer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LoginController {

    PageService service;

    @FXML
    TextField TextFieldUser;
    @FXML
    PasswordField passwordField;
    @FXML
    Button logInButton;


    public void setService(PageService service) {
        this.service = service;

    }


    public void seeLogIn(ActionEvent actionEvent) {
        String email =TextFieldUser.getText();
        System.out.println(email);

        String pass =passwordField.getText();
        String newPass = MD5.md5(pass);
        Utilizator u = service.cautaDupaEmail(email,newPass);
        if (u == null) {
            MessageAlert.showErrorMessage(null, "Datele sunt gresite. Verificati din nou.");
            return;
        }

        if (!u.getPassword().equals(newPass)) {
            MessageAlert.showErrorMessage(null, "Parola e gresita");
            return;
        }
        UserPage userPage = new UserPage(u.getId(),u.getLastName(),u.getFirstName());
        service.setUser(u);
        service.setUserPage(userPage);

        Stage stage = (Stage) logInButton.getScene().getWindow();
        MenuAction(u,stage);
        TextFieldUser.clear();
        passwordField.clear();
        stage.close();
    }


    public void signInAction(ActionEvent actionEvent) {


        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/signinView.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Sign In");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            SigninController controller= loader.getController();
            controller.setService(service);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void MenuAction(Utilizator u, Stage loginStage){

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/meniuView.fxml"));

            VBox root = (VBox) loader.load();
            BackgroundImage myBI= new BackgroundImage(new Image("images/white.jpg",400,400,false,true),
                    BackgroundRepeat.SPACE, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);
            root.setBackground(new Background(myBI));
            //root.setBackground(new Background(new BackgroundFill(Paint.valueOf("red"), CornerRadii.EMPTY, Insets.EMPTY)));

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Meniu");
            dialogStage.setWidth(580);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            MenuController controller= loader.getController();
            controller.setServices(service, u.getId(),loginStage);
            service.getEvenimentService().sendNotifications(u);


            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
