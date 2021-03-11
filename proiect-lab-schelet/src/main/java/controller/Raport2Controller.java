package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.CreatePdf;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.page.Page;
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

import static constant.PaginationConstant.PAGE_SIZE;

public class Raport2Controller  {
    UtilizatorService service;
    Utilizator user;   //user


    Stage primaryStage;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();


    @FXML
    TableColumn<Utilizator,String> tableColumnNume;
    @FXML
    TableColumn<Utilizator,String> tableColumnPrenume;
    @FXML
    Button buton;
    @FXML
    Pagination page;
    @FXML
    TableView<Utilizator> tableView;
    @FXML
    DatePicker inceput;
    @FXML
    DatePicker  sfarsit;


    public void setService(UtilizatorService service, Utilizator user) {
        this.service = service;
        this.user=user;

    }

    @FXML
    private void initialize() {
        tableColumnNume.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        tableColumnPrenume.setCellValueFactory(new PropertyValueFactory<>("FirstName"));

        page.setPageCount(1);
        initModel();

    }

    private Node createPage(int pageIndex) {


        //pageM.setCurrentPageIndex(1);
        Page<Utilizator> users =service.getFriendsOnPage(pageIndex,user.getId());

        model.setAll( StreamSupport.stream(users.getContent().spliterator(), false) .collect(Collectors.toList()));
        //    modelMesaj.setAll(messages.getContent());

        int size;
        if((users.getTotalCount()%PAGE_SIZE) ==0)
            size= users.getTotalCount()/PAGE_SIZE;
        else size=users.getTotalCount()/PAGE_SIZE+1;
        page.setPageCount(size);
        tableView.setItems(model);
        return tableView;

    }

    public void initModel(){

        page.setPageFactory(this::createPage);

    }




    public String arataDirector(ActionEvent ev) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("C:\\Users\\crist\\Documents"));

        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        System.out.println(selectedDirectory.getAbsolutePath());
        return selectedDirectory.getAbsolutePath();

    }


    public void raport2(ActionEvent event) {
        try {
            Utilizator prieten = tableView.getSelectionModel().getSelectedItem();
            String path = arataDirector(event);
            if (path.equals("") || path.equals(" ")) {
                MessageAlert.showErrorMessage(null, "Selectati directorul.");
                return;
            }

            System.out.println(path);
            path += "\\raport2.pdf";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            if (inceput.getEditor().getText().equals("") ||
                    sfarsit.getEditor().getText().equals("")) {
                MessageAlert.showErrorMessage(null, "O data e vida.Introduceti-o.");
                return;
            }
            String data = inceput.getEditor().getText();
            LocalDateTime d = LocalDate.parse(data, formatter).atStartOfDay();
            String datafinal = sfarsit.getEditor().getText();
            LocalDateTime d2 = LocalDate.parse(datafinal, formatter).atStartOfDay();
            List<String> rez = service.getMesajeDelaPrieten(user, prieten, d, d2);
            for (String s : rez) System.out.println(s);

            CreatePdf createPdf = new CreatePdf();

            createPdf.creare(rez, path);

            MessageAlert.showMessage(null, "Succes.");
            Stage stage = (Stage) buton.getScene().getWindow();
            stage.close();
        }
        catch(NullPointerException e){
            MessageAlert.showErrorMessage(null,"Selectati un prieten din tabel.");
            return;
        }


    }
    }

