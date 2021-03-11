package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.Prietenie;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;
import utils.events.PrietenieChangeEvent;
import utils.observers.Observer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PrietenieController implements Observer<PrietenieChangeEvent> {
    PrietenieService service;
    UtilizatorService utilizatorService;
    ObservableList<Prietenie> model = FXCollections.observableArrayList();


    @FXML
    TableView<Prietenie> tableView;
    @FXML
    TableColumn<Prietenie,Long> tableColumnU1;
    @FXML
    TableColumn<Prietenie,Long> tableColumnU2;
    @FXML
    TableColumn<Prietenie,String>  tableColumnData;



    public void setMessageTaskService(PrietenieService prietenieService) {
        service = prietenieService;
        service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        // TODO
        tableColumnU1.setCellValueFactory(new PropertyValueFactory<>("U1"));
        tableColumnU2.setCellValueFactory(new PropertyValueFactory<>("U2"));
        tableColumnData.setCellValueFactory(new PropertyValueFactory<>("date"));

        tableView.setItems(model);
    }

    private void initModel() {
        // TODO
        Iterable<Prietenie> messages = service.getPrieteni();
        List<Prietenie> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(messageTaskList);
    }

    @Override
    public void update(PrietenieChangeEvent ChangeEvent) {
        initModel();
    }

    public void handleDeleteMessage(ActionEvent actionEvent) {
        //sa apara mesaj daca s-a sters cu succes !!
         Prietenie m= tableView.getSelectionModel().getSelectedItem();
        System.out.println(m.toString());
        if(m!=null)
            service.stergePrietenie(m.getId().getLeft(),m.getId().getRight());
        else
            MessageAlert.showErrorMessage(null,"Nu ati selectat.");
    }


    @FXML
    public void handleUpdateMessage(ActionEvent ev) {
        Prietenie m= tableView.getSelectionModel().getSelectedItem();
        showMessageTaskEditDialog(m);

    }

    @FXML
    public void handleAddMessage(ActionEvent ev) {

        showMessageTaskEditDialog(null);


    }

    public void showMessageTaskEditDialog(Prietenie messageTask) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

//            EditPrietenieController edit = loader.getController();
//            edit.setService(service, dialogStage, messageTask);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
