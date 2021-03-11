package controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.EvenimentService;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class CreateEventViewCtr {
    @FXML
    private TextField textFieldNume;
    @FXML
    private TextArea descriereField;
    @FXML
    private TextField ora;
    @FXML
    DatePicker data;



    private EvenimentService service;

    Stage dialogStage;
    Utilizator curent;


    public void setService(EvenimentService service, Utilizator curent, Stage stage) {
        this.service = service;
        this.dialogStage=stage;
        this.curent=curent;


    }
    @FXML
    public void handleCancel(){
        dialogStage.close();
    }

    public void createEvent(ActionEvent event) {

        if(textFieldNume.getText().equals("")){
            MessageAlert.showErrorMessage(null,"Introduceti un nume.");
            return;
        }

        if(ora.getText().equals("")){
            MessageAlert.showErrorMessage(null,"Introduceti o ora(ora:minut).");
            return;
        }
        if(descriereField.getText().equals("")){
            MessageAlert.showErrorMessage(null,"Introduceti o descriere.");
            return;
        }
        try {
            String nume = textFieldNume.getText();
            String desc = descriereField.getText();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String dataI = data.getEditor().getText();
            if(dataI.equals(""))
            {
                MessageAlert.showErrorMessage(null,"Scrieti o data.");
                return;
            }
            LocalDateTime d = LocalDate.parse(dataI, formatter).atStartOfDay();
            String[] minutsiora= ora.getText().split(":",2);
            int ora= Integer.parseInt(minutsiora[0]);
            int minut= Integer.parseInt(minutsiora[1]);
            LocalDateTime dataFinala=d.toLocalDate().atTime(ora,minut);
            System.out.println(dataFinala);

            service.addEvent(curent.getId(), dataFinala, nume, desc);

            handleCancel();
        }
        catch (ValidationException e){
            MessageAlert.showErrorMessage(null, e.toString());
            return;
        }
    }
}

