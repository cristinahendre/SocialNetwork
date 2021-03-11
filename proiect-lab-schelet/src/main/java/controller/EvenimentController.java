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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.w3c.dom.Text;
import socialnetwork.domain.Eveniment;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.page.Page;
import socialnetwork.service.EvenimentService;
import socialnetwork.service.UtilizatorService;
import utils.events.EvenimentEvent;
import utils.events.UtilizatorChangeEvent;
import utils.observers.Observer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static constant.PaginationConstant.PAGE_SIZE;

public class EvenimentController  implements Observer<EvenimentEvent> {
    EvenimentService service;
    ObservableList<Eveniment> model = FXCollections.observableArrayList();
    private static final int rows=5;
    Utilizator curent;


    @FXML
    Button button;
    @FXML
    Pagination page;
    @FXML
    TextArea descriereText;
    @FXML
    TextArea participantiText;
    @FXML
    TableView<Eveniment> tableView;

    @FXML
    TableColumn<Eveniment, String>  tableColumnOrg;
    @FXML
    TableColumn<Eveniment, String>  tableColumnNume;

    @FXML
    TableColumn<Eveniment, String> tableColumnPart;
    @FXML
    TableColumn<Eveniment, String> tableColumnDataE;

    private int dataSize=12;


    public void setService(EvenimentService service, Utilizator curent) {
        this.service =service;
        this.curent=curent;
        dataSize=service.nrElem();
        service.addObserver(this);
        initModel();
    }




    @FXML
    public void initialize() {
        tableColumnOrg.setCellValueFactory(new PropertyValueFactory<>("Org"));
        tableColumnPart.setCellValueFactory(new PropertyValueFactory<>("ParticipantiString"));
        tableColumnDataE.setCellValueFactory(new PropertyValueFactory<>("Data"));
        tableColumnNume.setCellValueFactory(new PropertyValueFactory<>("Nume"));

        page.setPageCount(1);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);




    }

    private void initModel() {

        page.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {

        Page<Eveniment> all =service.getEventsOnPage(pageIndex,curent.getId());

        model.setAll( StreamSupport.stream(all.getContent().spliterator(), false) .collect(Collectors.toList()));


        int size;
        if((all.getTotalCount()%PAGE_SIZE) ==0)
            size= all.getTotalCount()/PAGE_SIZE;
        else size=all.getTotalCount()/PAGE_SIZE+1;
        page.setPageCount(size);
        tableView.setItems(model);
        return tableView;
    }


    public void inscriereEvent(ActionEvent event) {
        try{
            Eveniment[] events= tableView.getSelectionModel().getSelectedItems().toArray(new Eveniment[0]);
            for(Eveniment ev:events) {
                service.addParticipanti(ev.getId(), curent);
                //    service.notificareInscriere(ev,curent);
                service.primaNotificare(ev, curent);

            }
//            Stage stage = (Stage) button.getScene().getWindow();
//            stage.close();
            initModel();

        }
        catch (ValidationException e){
            MessageAlert.showErrorMessage(null, e.toString());
            return;
       }
        catch (NullPointerException e){
            MessageAlert.showErrorMessage(null,"Selectati din tabel.");
            return;
        }

    }

    @Override
    public void update(EvenimentEvent evenimentEvent) {
        initModel();
    }

    public void descriereGet(MouseEvent mouseEvent) {
        Eveniment e= tableView.getSelectionModel().getSelectedItem();
        descriereText.setText(e.getDescriere());
        participantiText.setText(e.getParticipantiAfisare());
    }
}