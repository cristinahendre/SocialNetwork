package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.CreatePdf;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UtilizatorService;
import utils.events.UtilizatorChangeEvent;
import utils.observers.Observer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RapoarteController  {
    UtilizatorService service;
    Utilizator user;   //user

    Stage primaryStage;

    @FXML
    Button buton;

    @FXML
    DatePicker DataPickerData;
    @FXML
    DatePicker  DataPickerDataFinal;


    public void setService(UtilizatorService service, Utilizator user) {
        this.service = service;
        this.user=user;

    }





    public void Raport1(ActionEvent actionEvent) {


        String path=arataDirector(actionEvent);
        if (path.equals("") || path.equals(" "))
        {
            MessageAlert.showErrorMessage(null,"Selectati directorul.");
            return;
        }

        System.out.println(path);

        path+="\\raport1.pdf";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String data = DataPickerData.getEditor().getText();
        LocalDateTime d = LocalDate.parse(data, formatter).atStartOfDay();
        String datafinal = DataPickerDataFinal.getEditor().getText();
        LocalDateTime d2 = LocalDate.parse(datafinal, formatter).atStartOfDay();

        List<String> rez = service.getMessagesAndFriendships(user.getId(), d, d2);
        for (String s : rez) {
            System.out.println(s);
        }
        CreatePdf createPdf = new CreatePdf();
        createPdf.creare(rez, path);

        MessageAlert.showMessage(null,"Succes.");
        Stage stage = (Stage) buton.getScene().getWindow();
        stage.close();
    }



    public String arataDirector(ActionEvent ev) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("C:\\Users\\crist\\Documents"));

        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        System.out.println(selectedDirectory.getAbsolutePath());
        return selectedDirectory.getAbsolutePath();

    }


}
