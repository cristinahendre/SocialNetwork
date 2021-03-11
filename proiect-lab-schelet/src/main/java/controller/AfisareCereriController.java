package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.CerereDePrietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.page.Page;
import socialnetwork.service.CerereDePrietenieService;
import socialnetwork.service.UtilizatorService;
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

import static constant.PaginationConstant.PAGE_SIZE;

public class AfisareCereriController implements Observer<CerereDePrietenieChangeEvent> {
    CerereDePrietenieService service;
    ObservableList<CerereDePrietenie> model = FXCollections.observableArrayList();
    long id;
    ObservableList<CerereDePrietenie> modelTrimise = FXCollections.observableArrayList();



    Stage dialogStage;
    @FXML
    TableView<CerereDePrietenie> tableView;
    @FXML
    Pagination pageC;
    @FXML
    TableView<CerereDePrietenie> tableViewTrimise;
    @FXML
    Pagination pageTrimise;

    @FXML
    TableColumn<CerereDePrietenie,Utilizator> tableColumnFrom;
    @FXML
    TableColumn<CerereDePrietenie,Utilizator> tableColumnTo;
    @FXML
    TableColumn<CerereDePrietenie,String> tableColumnStatus;
    @FXML
    TableColumn<CerereDePrietenie,String> tableColumnData;

    @FXML
    TableColumn<CerereDePrietenie,Utilizator> tableColumnFromT;
    @FXML
    TableColumn<CerereDePrietenie,Utilizator> tableColumnToT;
    @FXML
    TableColumn<CerereDePrietenie,String> tableColumnStatusT;
    @FXML
    TableColumn<CerereDePrietenie,String> tableColumnDataT;
    @FXML
    Button aButton;
    @FXML
    Button delButton;
    @FXML
    Button rejButton;



    public void setService(CerereDePrietenieService service,Stage stage, long id) {
        this.service = service;
        service.addObserver(this);
        this.id=id;

        this.dialogStage=stage;

    }

    @FXML
    public void initialize() {

        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<>("NameTrimite"));
        tableColumnTo.setCellValueFactory(new PropertyValueFactory<>("NamePrimeste"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("Status"));
        tableColumnData.setCellValueFactory(new PropertyValueFactory<>("Data"));

        tableColumnFromT.setCellValueFactory(new PropertyValueFactory<>("NameTrimite"));
        tableColumnToT.setCellValueFactory(new PropertyValueFactory<>("NamePrimeste"));
        tableColumnStatusT.setCellValueFactory(new PropertyValueFactory<>("Status"));
        tableColumnDataT.setCellValueFactory(new PropertyValueFactory<>("Data"));
        initTrimise();
        initPrimite();

        tableViewTrimise.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void initTrimise() {
        pageTrimise.setPageFactory(this::createTrimise);

    }

    private Node createTrimise(int pageIndex) {

        Page<CerereDePrietenie> all =service.getCereriTrimise(pageIndex,id);

        modelTrimise.setAll( StreamSupport.stream(all.getContent().spliterator(), false) .collect(Collectors.toList()));

        int size;
        if((all.getTotalCount()%PAGE_SIZE) ==0)
            size= all.getTotalCount()/PAGE_SIZE;
        else size=all.getTotalCount()/PAGE_SIZE+1;
        pageTrimise.setPageCount(size);
        tableViewTrimise.setItems(modelTrimise);
        return tableViewTrimise;

    }


    private void initPrimite() {
        pageC.setPageFactory(this::createPrimite);

    }

    private Node createPrimite(int pageIndex) {

        Page<CerereDePrietenie> all =service.getCereriPrimite(pageIndex,id);

        model.setAll( StreamSupport.stream(all.getContent().spliterator(), false) .collect(Collectors.toList()));

        int size;
        if((all.getTotalCount()%PAGE_SIZE) ==0)
            size= all.getTotalCount()/PAGE_SIZE;
        else size=all.getTotalCount()/PAGE_SIZE+1;
        pageC.setPageCount(size);
        tableView.setItems(model);
        return tableView;

    }


    public void deleteC(){
        try {
            CerereDePrietenie[] cereri = tableViewTrimise.getSelectionModel().getSelectedItems().toArray(new CerereDePrietenie[0]);
            if (cereri.length == 0) {
                MessageAlert.showErrorMessage(null, "Nu ati selectat.");
                return;
            }
            for(CerereDePrietenie pr: cereri) {
                if (!pr.getStatus().equals("pending")) {
                    MessageAlert.showErrorMessage(null, "Cererea nu poate fi stearsa.");
                    return;
                }
                if (!pr.getTrimite().getId().equals(id)) {
                    MessageAlert.showErrorMessage(null, "Cererea nu poate fi stearsa-nu ati trimis-o dvs.");
                    return;
                }
                service.stergeCerere(pr.getId());
                initTrimise();
            }
        }
        catch(NullPointerException e){
            MessageAlert.showErrorMessage(null,"Nu ati selectat.");
        }
    }


    @Override
    public void update(CerereDePrietenieChangeEvent cerereDePrietenieChangeEvent) {
        initTrimise();
    }




    public void rejectC(){

        try {

                 CerereDePrietenie[] cereri = tableView.getSelectionModel().getSelectedItems().toArray(new CerereDePrietenie[0]);
                 if (cereri.length==0) {
                     MessageAlert.showErrorMessage(null, "Nu ati selectat.");
                     return;
                 }

                 for(CerereDePrietenie pr: cereri) {
                     if (!pr.getStatus().equals("pending")) {
                         MessageAlert.showErrorMessage(null, "Cererea nu poate fi aprobata/respinsa.");
                         return;
                     }
                     if (pr.getPrimeste().getId().equals(id)) {

                         service.modificaCerere(pr.getId(), "rejected");

                     } else {
                         MessageAlert.showErrorMessage(null, "Nu puteti accepta/refuza cererea de prietenie pe care ati trimis-o.");
                         return;
                     }
                 }

             } catch (NullPointerException e) {
                 MessageAlert.showErrorMessage(null,"Nu ati selectat.");


             }
        initPrimite();



    }

    public void approveC(ActionEvent actionEvent) {

        try {
                CerereDePrietenie[] cereri  = tableView.getSelectionModel().getSelectedItems().toArray(new CerereDePrietenie[0]);
                if (cereri.length  == 0) {
                    MessageAlert.showErrorMessage(null, "Nu ati selectat.");
                    return;
                }
                for(CerereDePrietenie pr: cereri) {
                    if (!pr.getStatus().equals("pending")) {
                        MessageAlert.showErrorMessage(null, "Cererea nu poate fi aprobata/respinsa.");
                        return;
                    }
                    if (pr.getPrimeste().getId().equals(id)) {

                        service.modificaCerere(pr.getId(), "approved");

                    } else {
                        MessageAlert.showErrorMessage(null, "Nu puteti accepta/refuza cererea de prietenie pe care ati trimis-o.");
                        return;
                    }
                }

            } catch (NullPointerException e) {
                //MessageAlert.showErrorMessage(null,"Nu ati selectat.");
                return;

            }
        initPrimite();

    }

    public void disableButtons() {
        try {
            tableViewTrimise.getSelectionModel().clearSelection();
            CerereDePrietenie cerere = tableView.getSelectionModel().getSelectedItem();

            aButton.setDisable(false);
            rejButton.setDisable(false);
            if (!cerere.getStatus().equals("pending")) {
                aButton.setDisable(true);
                rejButton.setDisable(true);
            }
            delButton.setDisable(true);
        }
        catch (NullPointerException e){
            return;
        }
    }

    public void disableButtons2() {
        try {

            delButton.setDisable(false);
            tableView.getSelectionModel().clearSelection();

            CerereDePrietenie cerere = tableViewTrimise.getSelectionModel().getSelectedItem();

            if (!cerere.getStatus().equals("pending")) {
                delButton.setDisable(true);

            }
            rejButton.setDisable(true);
            aButton.setDisable(true);
        }
        catch (NullPointerException e){
            return;
        }


    }
}
