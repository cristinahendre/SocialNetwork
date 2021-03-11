package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.MD5;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.PageService;
import socialnetwork.service.UtilizatorService;


public class SigninController {

    PageService service;

    @FXML
    TextField textFieldNume;
    @FXML
    TextField textFieldPrenume;
    @FXML
    PasswordField textFieldParola;
    @FXML
    TextField textFieldEmail;
    @FXML
    Button buttonToSave;

    public void setService(PageService service){
        this.service=service;
    }


    /**
     * Adauga un nou user in baza de date
     * @param actionEvent ->nefolosit
     */
    public void saveUser(ActionEvent actionEvent) {
        String nume= textFieldNume.getText();
        if(nume.equals("")){
            MessageAlert.showErrorMessage(null,"Nume vid");
            return;
        }
        System.out.println(nume);
        String prenume= textFieldPrenume.getText();
        if(prenume.equals("")){
            MessageAlert.showErrorMessage(null,"Prenume vid");
            return;
        }
        String email = textFieldEmail.getText();
        if(email.equals("")){
            MessageAlert.showErrorMessage(null,"Email vid");
            return;
        }
        String parola= textFieldParola.getText();
        if(parola.equals("")){
            MessageAlert.showErrorMessage(null,"Parola e vida");
            return;
        }
        String newPass= MD5.md5(parola);


        Utilizator user=new Utilizator(nume,prenume);
        user.setEmail(email);
        user.setPassword(newPass);
        try {
            service.addUtilizator(user);
            MessageAlert.showMessage(null,"Logati-va.");
            Stage stage = (Stage) buttonToSave.getScene().getWindow();
            stage.close();
        }
        catch(ValidationException e){
            MessageAlert.showErrorMessage(null,e.toString());
            return;
        }



    }
}
